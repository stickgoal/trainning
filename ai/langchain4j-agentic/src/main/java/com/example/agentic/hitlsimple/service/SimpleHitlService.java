package com.example.agentic.hitlsimple.service;

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
 * 极简版 HumanInTheLoop —— 只为讲清原理，不依赖任何 Agent 框架 / 数据库。
 *
 * <h3>HumanInTheLoop 到底在解决什么问题？</h3>
 * 一条自动流程跑到某一步，需要「人」拍板（审批 / 纠错 / 补全信息）才能继续。
 * 难点在于：HTTP 请求是「一来一回」的，不能在「等人工」时一直占着线程不返回。
 * 所以必须把流程<b>切成两段</b>，用「挂起状态 + 关联令牌」把它们接起来：
 *
 * <pre>
 *   提交(HTTP-A) ──► [自动：审批前步骤] ──► 挂起，返回 requestId + 状态=PENDING
 *
 *        …………  人工在别处看到 requestId，做出决策  …………
 *
 *   审批(HTTP-B) ──► 凭 requestId 找回挂起状态 ──► [自动：审批后步骤] ──► 返回最终结果
 * </pre>
 *
 * <h3>三个核心要素（记住这三样，HITL 就懂了）</h3>
 * <ol>
 *   <li><b>挂起状态（Suspended State）</b>：流程暂停时，把「审批前已经算出来的上下文」
 *       整体保存下来——本实现用 {@code store} 这个内存 Map 保存 {@link PendingRequest}。</li>
 *   <li><b>关联令牌（Correlation Token）</b>：{@code requestId}。它是连接两条 HTTP 请求的钥匙，
 *       审批时靠它把挂起状态找回来。对应框架里的 {@code PendingResponse.responseId}。</li>
 *   <li><b>人工决策通道（Human Channel）</b>：一条独立的接口（{@code /approve}），专门接收人的输入。
 *       它和「流程执行」解耦——这正是 HITL 与「普通多 Agent 编排」的根本区别。</li>
 * </ol>
 *
 * <h3>与 LangChain4j 官方 {@code @HumanInTheLoop} 的对照</h3>
 * <table border="1" cellpadding="4">
 *   <tr><th>本极简实现</th><th>官方框架</th><th>说明</th></tr>
 *   <tr><td>{@code store} + {@code requestId}</td><td>{@code AgenticScope} + {@code PendingResponse}</td><td>都是为了「暂停并找回状态」</td></tr>
 *   <tr><td>两条 HTTP（submit / approve）</td><td>{@code blockingGet()} 挂起 / {@code completePendingResponse()} 恢复</td><td>框架在一個调用内用线程阻塞串起挂起-恢复；Web 演示用两条请求更直观</td></tr>
 *   <tr><td>内存 Map</td><td>可序列化落库，支持进程重启恢复</td><td>生产环境应持久化，避免重启丢状态</td></tr>
 * </table>
 *
 * <p><b>为什么「审批前 / 审批后」用确定性规则而不是 LLM？</b>
 * 为了把注意力完全放在 HITL 机制上。真实项目里这两步通常就是 LLM Agent 调用，
 * 把 {@link #preCheck} 和 {@link #executeRefund} 换成 Agent 调用即可，骨架不变。</p>
 */
@Slf4j
@Service
public class SimpleHitlService {

    /**
     * 挂起状态存储。submit 时写入，approve 时读出并消费。
     * 真实系统会持久化到 DB / Redis；这里用内存 Map 只为把原理讲透。
     * 注意：进程重启后内容会清空（所以演示完要及时审批，或看官方版做持久化）。
     */
    private final Map<String, PendingRequest> store = new ConcurrentHashMap<>();

    // =========================================================================
    //  第一段：提交 → 自动预处理 → 挂起（返回 requestId）
    // =========================================================================

    /**
     * 提交退款申请：跑完「审批前」自动步骤，挂起，返回一个 requestId 给前端。
     * 关键点：本方法<b>立即返回</b>，不会卡住等人工——这正是 Web 场景下 HITL 的标准写法。
     */
    public SimpleHitlResponse submit(String orderId, String reason, double amount) {
        log.info("[HITL] submit: orderId={}, reason={}, amount={}", orderId, reason, amount);

        // ① 自动预处理（确定性规则，可替换为 LLM Agent）
        String level = riskLevel(reason, amount);
        String preCheck = preCheckText(orderId, reason, amount, level);

        // ② 生成关联令牌
        String requestId = "HITL-" + UUID.randomUUID().toString().substring(0, 8);

        // ③ 挂起：把上下文整体保存，状态置为 PENDING
        PendingRequest pr = PendingRequest.builder()
                .requestId(requestId)
                .orderId(orderId)
                .reason(reason)
                .amount(amount)
                .level(level)
                .preCheck(preCheck)
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();
        store.put(requestId, pr);

        log.info("[HITL] 已挂起，等待人工审批。requestId={}", requestId);
        return SimpleHitlResponse.pending(pr,
                "已暂停在人工审批点。请把 requestId 交给审批人，调用 /approve 完成审批");
    }

    // =========================================================================
    //  第二段：审批 → 找回挂起状态 → 自动后置执行 → 返回最终结果
    // =========================================================================

    /**
     * 人工审批：凭 requestId 找回挂起状态，注入人的决策，跑完「审批后」自动步骤。
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

        // ② 自动后置执行（确定性规则，可替换为 LLM Agent / 业务系统调用）
        pr.setResult(approved ? executeRefund(pr) : rejectRefund(pr));

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
    //  以下两个方法 = 「审批前 / 审批后」自动步骤。真实项目里换成 LLM Agent 即可。
    // =========================================================================

    /** 审批前步骤：风控分级（确定性规则，便于讲解，不调 LLM）。 */
    private String riskLevel(String reason, double amount) {
        if (amount >= 5000) {
            return "高";
        }
        if (reason != null && (reason.contains("破损") || reason.contains("假货") || reason.contains("质量"))) {
            return "中";
        }
        return "低";
    }

    private String preCheckText(String orderId, String reason, double amount, String level) {
        String advice = switch (level) {
            case "高" -> "金额较大，必须主管人工审批";
            case "中" -> "疑似质量问题，建议人工确认";
            default -> "常规退款，可走自动审批";
        };
        return "订单 " + orderId + " 申请退款 " + amount + " 元，原因：" + reason
                + "。风控等级【" + level + "】：" + advice + "。";
    }

    /** 审批后步骤（通过）：执行退款，产出最终结果文本。 */
    private String executeRefund(PendingRequest pr) {
        return "✅ 退款已受理：订单 " + pr.getOrderId() + " 退款 " + pr.getAmount()
                + " 元（风控等级：" + pr.getLevel() + "）。系统已发起退款工单，预计 1-3 个工作日到账。";
    }

    /** 审批后步骤（驳回）：产出最终结果文本。 */
    private String rejectRefund(PendingRequest pr) {
        String why = (pr.getComment() != null && !pr.getComment().isBlank())
                ? pr.getComment() : pr.getDecision();
        return "❌ 退款已被人工驳回（依据：" + why + "）。已通知用户，工单关闭。";
    }

    private boolean isApproved(String decision) {
        return decision != null && (decision.equalsIgnoreCase("APPROVED")
                || decision.contains("同意") || decision.contains("通过"));
    }
}
