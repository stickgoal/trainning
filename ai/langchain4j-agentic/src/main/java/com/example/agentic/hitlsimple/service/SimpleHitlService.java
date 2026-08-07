package com.example.agentic.hitlsimple.service;

import com.example.agentic.hitlsimple.agent.HitlAgent;
import com.example.agentic.hitlsimple.model.PendingRequest;
import com.example.agentic.hitlsimple.model.SimpleHitlResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 极简版 HumanInTheLoop —— 只为讲清「挂起/恢复」机制；但<b>任务的执行交给真实 LLM Agent</b>。
 *
 * <h3>HumanInTheLoop 到底在解决什么问题？</h3>
 * 一条自动流程跑到某一步，需要「人」拍板（审批 / 纠错 / 补全信息）才能继续。
 * 难点在于：HTTP 请求是「一来一回」的，不能在「等人工」时一直占着线程不返回。
 * 所以必须把流程<b>切成两段</b>，用「挂起状态 + 关联令牌」把它们接起来：
 *
 * <pre>
 *   提交(HTTP-A) ──► [Agent 跑"审批前"分析] ──► 挂起，返回 requestId + 状态=PENDING
 *
 *        …………  人工在别处看到 requestId，做出决策  …………
 *
 *   审批(HTTP-B) ──► 凭 requestId 找回挂起状态 ──► [Agent 按人决策跑"审批后"执行] ──► 返回结果
 * </pre>
 *
 * <h3>三个核心要素（记住这三样，HITL 就懂了）</h3>
 * <ol>
 *   <li><b>挂起状态（Suspended State）</b>：流程暂停时，把「审批前 Agent 已算出的上下文」
 *       整体保存下来——本实现用 {@code store} 这个内存 Map 保存 {@link PendingRequest}。</li>
 *   <li><b>关联令牌（Correlation Token）</b>：{@code requestId}。它是连接两条 HTTP 请求的钥匙，
 *       审批时靠它把挂起状态找回来。对应框架里的 {@code PendingResponse.responseId}。</li>
 *   <li><b>人工决策通道（Human Channel）</b>：一条独立的接口（{@code /approve}），专门接收人的输入。
 *       它和「流程执行」解耦——这正是 HITL 与「普通多 Agent 编排」的根本区别。</li>
 * </ol>
 *
 * <h3>本模块与官方 {@code @HumanInTheLoop} 的对照</h3>
 * <table border="1" cellpadding="4">
 *   <tr><th>本极简实现</th><th>官方框架</th><th>说明</th></tr>
 *   <tr><td>{@code store} + {@code requestId}</td><td>{@code AgenticScope} + {@code PendingResponse}</td><td>都是为了「暂停并找回状态」</td></tr>
 *   <tr><td>两条 HTTP（submit / approve）</td><td>{@code blockingGet()} 挂起 / {@code completePendingResponse()} 恢复</td><td>框架在一个调用内用线程阻塞串起挂起-恢复；Web 演示用两条请求更直观</td></tr>
 *   <tr><td>内存 Map</td><td>可序列化落库，支持进程重启恢复</td><td>生产环境应持久化，避免重启丢状态</td></tr>
 *   <tr><td>{@code HitlAgent}（AiServices 包装的真实 LLM）</td><td>声明式 {@code @Agent} 子 Agent</td><td>两者都是真调大模型，只是装配方式不同</td></tr>
 * </table>
 *
 * <p><b>本模块的关键变化</b>：「审批前 / 审批后」不再是 if/else 模拟，而是调用
 * {@link HitlAgent} 让大模型真实推理——分析退款、按人决策执行。大模型偶尔不可用（无网/无 key），
 * 因此两处都保留了<b>确定性兜底</b>，保证演示永远不崩；但正常情况下跑的是真 AI。</p>
 */
@Slf4j
@Service
public class SimpleHitlService {

    /**
     * 真正的「任务执行 Agent」：走 LangChain4j AiServices + 真实大模型。
     * 它只负责干活（分析 / 执行），不负责挂起机制。
     */
    private final HitlAgent agent;

    /**
     * 挂起状态存储。submit 时写入，approve 时读出并消费。
     * 真实系统会持久化到 DB / Redis；这里用内存 Map 只为把原理讲透。
     * 注意：进程重启后内容会清空（所以演示完要及时审批，或看官方版做持久化）。
     */
    private final Map<String, PendingRequest> store = new ConcurrentHashMap<>();

    public SimpleHitlService(HitlAgent agent) {
        this.agent = agent;
    }

    // =========================================================================
    //  第一段：提交 → Agent 预处理 → 挂起（返回 requestId）
    // =========================================================================

    /**
     * 提交退款申请：让 Agent 跑完「审批前」分析，挂起，返回一个 requestId 给前端。
     * 关键点：本方法<b>立即返回</b>，不会卡住等人工——这正是 Web 场景下 HITL 的标准写法。
     */
    public SimpleHitlResponse submit(String orderId, String reason, double amount) {
        log.info("[HITL] submit: orderId={}, reason={}, amount={}", orderId, reason, amount);

        // ① 让真实 Agent 做「审批前」智能分析（大模型推理，非规则模拟）
        String preCheck;
        try {
            preCheck = agent.preCheck(orderId, reason, amount);
        } catch (Exception e) {
            log.warn("[HITL] Agent 预处理调用失败，启用确定性兜底：{}", e.getMessage());
            preCheck = fallbackPreCheck(orderId, reason, amount);
        }

        // ② 生成关联令牌
        String requestId = "HITL-" + UUID.randomUUID().toString().substring(0, 8);

        // ③ 挂起：把上下文整体保存，状态置为 PENDING
        PendingRequest pr = PendingRequest.builder()
                .requestId(requestId)
                .orderId(orderId)
                .reason(reason)
                .amount(amount)
                .level(deriveLevel(preCheck, reason, amount)) // 等级仅为 UI 速览标签，真正分析见 preCheck
                .preCheck(preCheck)
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();
        store.put(requestId, pr);

        log.info("[HITL] 已挂起，等待人工审批。requestId={}", requestId);
        return SimpleHitlResponse.pending(pr,
                "AI 已完成审批前分析，流程暂停在人工审批点。请把 requestId 交给审批人，调用 /approve 完成审批");
    }

    // =========================================================================
    //  第二段：审批 → 找回挂起状态 → Agent 后置执行 → 返回最终结果
    // =========================================================================

    /**
     * 人工审批：凭 requestId 找回挂起状态，注入人的决策，让 Agent 跑完「审批后」执行。
     */
    public SimpleHitlResponse approve(String requestId, String decision, String comment) {
        log.info("[HITL] approve: requestId={}, decision={}, comment={}", requestId, decision, comment);

        PendingRequest pr = store.get(requestId);
        if (pr == null) {
            return SimpleHitlResponse.notFound(requestId);
        }
        // 幂等保护：已经审批过的，直接返回原结果，不重复执行「审批后」步骤
        if (!"PENDING".equals(pr.getStatus())) {
            return SimpleHitlResponse.alreadyDone(pr);
        }

        // ① 注入人工决策
        boolean approved = isApproved(decision);
        pr.setDecision(decision);
        pr.setComment(comment);

        // ② 让真实 Agent 依据「人工决策 + 前面分析材料」执行退款（大模型推理）
        String result;
        try {
            result = agent.execute(pr.getOrderId(), pr.getAmount(), pr.getPreCheck(), decision, comment);
        } catch (Exception e) {
            log.warn("[HITL] Agent 执行调用失败，启用确定性兜底：{}", e.getMessage());
            result = approved ? fallbackExecute(pr) : fallbackReject(pr);
        }
        pr.setResult(result);

        // ③ 更新状态
        pr.setStatus(approved ? "APPROVED" : "REJECTED");
        pr.setUpdatedAt(LocalDateTime.now());

        log.info("[HITL] 审批完成，requestId={}, status={}", requestId, pr.getStatus());
        return SimpleHitlResponse.completed(pr);
    }

    // =========================================================================
    //  辅助：查询 / 列出待审批（便于演示与讲解）
    // =========================================================================

    /** 查看某个 requestId 的当前状态（无论是否还在等待）。 */
    public SimpleHitlResponse status(String requestId) {
        PendingRequest pr = store.get(requestId);
        if (pr == null) {
            return SimpleHitlResponse.notFound(requestId);
        }
        if ("PENDING".equals(pr.getStatus())) {
            return SimpleHitlResponse.pending(pr, "仍在等待人工审批");
        }
        return SimpleHitlResponse.completed(pr);
    }

    /** 列出所有还在等待人工审批的请求——直观展示「有多少人正卡在 HITL 上」。 */
    public List<PendingRequest> listPending() {
        List<PendingRequest> pending = new ArrayList<>();
        for (PendingRequest pr : store.values()) {
            if ("PENDING".equals(pr.getStatus())) {
                pending.add(pr);
            }
        }
        return pending;
    }

    // =========================================================================
    //  等级标签 + 确定性兜底（仅在 Agent 调用失败时使用，保证演示不崩）
    // =========================================================================

    /**
     * 风险等级标签：优先从 Agent 的 preCheck 文本里识别"高/中/低"，识别不到再走轻量启发式。
     * 注意：它只是个 UI 速览标签，真正的分析在 {@code preCheck} 文本里（由 AI 产出）。
     */
    private String deriveLevel(String preCheck, String reason, double amount) {
        if (preCheck != null) {
            if (preCheck.contains("风险等级") || preCheck.contains("风险初判")) {
                if (preCheck.contains("高")) return "高";
                if (preCheck.contains("中")) return "中";
                if (preCheck.contains("低")) return "低";
            }
        }
        if (amount >= 5000) return "高";
        if (reason != null && (reason.contains("破损") || reason.contains("假货") || reason.contains("质量"))) return "中";
        return "低";
    }

    /** 兜底：Agent 不可用时的「审批前」材料。 */
    private String fallbackPreCheck(String orderId, String reason, double amount) {
        String level = deriveLevel(null, reason, amount);
        String advice = switch (level) {
            case "高" -> "金额较大，必须主管人工审批";
            case "中" -> "疑似质量问题，建议人工确认";
            default -> "常规退款，可走自动审批";
        };
        return "[兜底] 订单 " + orderId + " 申请退款 " + amount + " 元，原因：" + reason
                + "。风控等级【" + level + "】：" + advice + "。";
    }

    /** 兜底：Agent 不可用时的「审批后（通过）」结果。 */
    private String fallbackExecute(PendingRequest pr) {
        return "[兜底] ✅ 退款已受理：订单 " + pr.getOrderId() + " 退款 " + pr.getAmount()
                + " 元（风控等级：" + pr.getLevel() + "）。系统已发起退款工单，预计 1-3 个工作日到账。";
    }

    /** 兜底：Agent 不可用时的「审批后（驳回）」结果。 */
    private String fallbackReject(PendingRequest pr) {
        String why = (pr.getComment() != null && !pr.getComment().isBlank()) ? pr.getComment() : pr.getDecision();
        return "[兜底] ❌ 退款已被人工驳回（依据：" + why + "）。已通知用户，工单关闭。";
    }

    private boolean isApproved(String decision) {
        return decision != null && (decision.equalsIgnoreCase("APPROVED")
                || decision.contains("同意") || decision.contains("通过"));
    }
}
