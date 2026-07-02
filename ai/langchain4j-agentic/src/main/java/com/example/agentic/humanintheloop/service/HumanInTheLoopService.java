package com.example.agentic.humanintheloop.service;

import com.example.agentic.common.tool.AfterSalesTools;
import com.example.agentic.humanintheloop.HumanApprovalWorkflow;
import com.example.agentic.humanintheloop.agent.ExecuteAgent;
import com.example.agentic.humanintheloop.agent.PreCheckAgent;
import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.agentic.internal.PendingResponse;
import dev.langchain4j.agentic.scope.AgenticScope;
import dev.langchain4j.agentic.scope.AgenticScopeAccess;
import dev.langchain4j.agentic.workflow.HumanInTheLoop;
import dev.langchain4j.model.chat.ChatModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * HumanInTheLoop 人工介入工作流服务。
 * 
 * 与其它模式不同，本模式需要「暂停 → 等待人工 → 恢复」。实现要点：
 * <ol>
 *   <li>PreCheckAgent → HumanInTheLoop → ExecuteAgent 组成顺序工作流；</li>
 *   <li>HumanInTheLoop 的 responseProvider 返回一个 {@link PendingResponse}，
 *       写入 AgenticScope 的 "managerApproval"，此时并不阻塞；</li>
 *   <li>工作流放在后台线程执行，当 ExecuteAgent 读取 "managerApproval" 时会阻塞，
 *       从而实现「暂停」；</li>
 *   <li>外部通过 REST 接口调用 {@link AgenticScope#completePendingResponse}
 *       注入审批结果，后台线程随即「恢复」并完成执行。</li>
 * </ol>
 */
@Slf4j
@Service
public class HumanInTheLoopService {

    /** HumanInTheLoop 暂停点在 AgenticScope 中使用的 key，同时作为 PendingResponse 的 responseId。 */
    public static final String APPROVAL_KEY = "managerApproval";

    private final HumanApprovalWorkflow workflow;
    private final ExecutorService executor = Executors.newCachedThreadPool();
    /** requestId → 后台运行中的工作流 Future。 */
    private final Map<String, Future<String>> runningWorkflows = new ConcurrentHashMap<>();

    @Autowired
    public HumanInTheLoopService(ChatModel chatModel, AfterSalesTools tools) {
        log.info("Building HumanInTheLoopWorkflow with chatModel={}", chatModel);

        // 1. 前置检查 Agent
        PreCheckAgent preCheckAgent = AgenticServices
                .agentBuilder(PreCheckAgent.class)
                .chatModel(chatModel)
                .tools(tools)
                .outputKey("preCheckResult")
                .build();
        log.info("PreCheckAgent built: {}", preCheckAgent);

        // 2. 人工介入 Agent：返回一个待完成的 PendingResponse，写入 "managerApproval"
        HumanInTheLoop humanApprovalAgent = AgenticServices
                .humanInTheLoopBuilder()
                .description("暂停工作流，等待售后主管对大额退款进行人工审批")
                .outputKey(APPROVAL_KEY)
                .async(false)
                .responseProvider(scope -> {
                    log.info("[HumanInTheLoop] workflow paused, waiting for manager approval (responseId={})", APPROVAL_KEY);
                    return new PendingResponse<String>(APPROVAL_KEY);
                })
                .build();
        log.info("HumanInTheLoop agent built: {}", humanApprovalAgent);

        // 3. 执行 Agent
        ExecuteAgent executeAgent = AgenticServices
                .agentBuilder(ExecuteAgent.class)
                .chatModel(chatModel)
                .tools(tools)
                .outputKey("executionResult")
                .build();
        log.info("ExecuteAgent built: {}", executeAgent);

        // 4. 顺序组装：前置检查 → 人工审批(暂停点) → 执行
        this.workflow = AgenticServices
                .sequenceBuilder(HumanApprovalWorkflow.class)
                .subAgents(preCheckAgent, humanApprovalAgent, executeAgent)
                .outputKey("executionResult")
                .build();
        log.info("HumanApprovalWorkflow built: {}", workflow);
    }

    /**
     * 提交大额退款申请，运行至人工审批环节暂停。
     *
     * @return 包含 requestId、状态、前置检查材料的信息，供人工审批参考
     */
    public Map<String, Object> submitRefund(String orderId, String reason, double amount) {
        String requestId = "REQ-" + UUID.randomUUID().toString().substring(0, 8);
        log.info("submitRefund: requestId={}, orderId={}, reason={}, amount={}", requestId, orderId, reason, amount);

        // 后台线程运行工作流：它会在 ExecuteAgent 读取 managerApproval 时阻塞
        Future<String> future = executor.submit(() -> workflow.process(requestId, orderId, reason, amount));
        runningWorkflows.put(requestId, future);

        // 等待工作流到达暂停点（PendingResponse 已写入）或提前结束
        AgenticScope scope = awaitPause(requestId, future);

        if (future.isDone()) {
            // 理论上不会发生（HumanInTheLoop 一定会暂停），但仍做兜底处理
            String result = getResultQuietly(requestId, future);
            return Map.of(
                "requestId", requestId,
                "status", "COMPLETED",
                "executionResult", result
            );
        }

        String preCheckResult = scope != null ? scope.readState("preCheckResult", "") : "";
        return Map.of(
            "requestId", requestId,
            "orderId", orderId,
            "reason", reason,
            "amount", amount,
            "status", "PENDING_APPROVAL",
            "preCheckResult", preCheckResult,
            "hint", "调用 POST /api/humanintheloop/approve?requestId=" + requestId
                    + "&decision=APPROVED|REJECTED 以完成审批"
        );
    }

    /**
     * 注入人工审批结果，恢复工作流并返回最终执行结果。
     *
     * @param requestId 审批请求ID
     * @param decision  审批结论：APPROVED 或 REJECTED
     * @param comment   审批备注（可选）
     */
    public Map<String, Object> approve(String requestId, String decision, String comment) {
        log.info("approve: requestId={}, decision={}, comment={}", requestId, decision, comment);

        Future<String> future = runningWorkflows.get(requestId);
        if (future == null) {
            return Map.of("requestId", requestId, "status", "NOT_FOUND",
                    "message", "未找到对应的待审批请求，可能已处理完成或不存在");
        }

        AgenticScope scope = ((AgenticScopeAccess) workflow).getAgenticScope(requestId);
        if (scope == null) {
            return Map.of("requestId", requestId, "status", "NOT_FOUND",
                    "message", "未找到对应的会话上下文");
        }

        String approvalText = decision + (comment == null || comment.isBlank() ? "" : " - " + comment);
        boolean accepted = scope.completePendingResponse(APPROVAL_KEY, approvalText);
        log.info("approve: completePendingResponse accepted={} for requestId={}", accepted, requestId);
        if (!accepted) {
            return Map.of("requestId", requestId, "status", "ALREADY_HANDLED",
                    "message", "该请求已被审批或不处于等待状态");
        }

        // 工作流恢复执行，等待最终结果
        String result;
        try {
            result = future.get(120, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("approve: workflow execution failed for requestId={}", requestId, e);
            return Map.of("requestId", requestId, "status", "ERROR",
                    "message", "工作流恢复执行失败: " + e.getMessage());
        } finally {
            runningWorkflows.remove(requestId);
            ((AgenticScopeAccess) workflow).evictAgenticScope(requestId);
        }

        return Map.of(
            "requestId", requestId,
            "decision", decision,
            "status", "COMPLETED",
            "executionResult", result
        );
    }

    /**
     * 轮询等待工作流到达人工审批暂停点。
     * 暂停点标志：AgenticScope 已注册且 pendingResponseIds 含 APPROVAL_KEY。
     */
    private AgenticScope awaitPause(String requestId, Future<String> future) {
        AgenticScopeAccess access = (AgenticScopeAccess) workflow;
        long deadline = System.currentTimeMillis() + 60_000L;
        while (System.currentTimeMillis() < deadline) {
            if (future.isDone()) {
                return access.getAgenticScope(requestId);
            }
            AgenticScope scope = access.getAgenticScope(requestId);
            if (scope != null && scope.pendingResponseIds().contains(APPROVAL_KEY)) {
                return scope;
            }
            sleep(200);
        }
        log.warn("awaitPause: timed out waiting for pause point, requestId={}", requestId);
        return access.getAgenticScope(requestId);
    }

    private String getResultQuietly(String requestId, Future<String> future) {
        try {
            return future.get(1, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("getResultQuietly failed for requestId={}", requestId, e);
            return "工作流执行异常: " + e.getMessage();
        } finally {
            runningWorkflows.remove(requestId);
            ((AgenticScopeAccess) workflow).evictAgenticScope(requestId);
        }
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
