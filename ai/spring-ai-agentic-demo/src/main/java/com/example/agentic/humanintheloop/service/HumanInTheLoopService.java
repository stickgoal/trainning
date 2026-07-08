package com.example.agentic.humanintheloop.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * HumanInTheLoop 人工介入工作流服务。
 * <p>
 * Spring AI 迁移说明：
 * - LangChain4j 的 humanInTheLoopBuilder + PendingResponse → CompletableFuture + CountDownLatch
 * - PreCheckAgent → 暂停等待人工审批 → ExecuteAgent
 * - 工作流在后台线程执行，通过 CountDownLatch 实现暂停/恢复
 * <p>
 * 流程：
 *   PreCheckAgent（前置检查）→ 暂停等待人工审批 → ExecuteAgent（执行退款）
 */
@Slf4j
@Service
public class HumanInTheLoopService {

    private final ChatClient chatClient;
    private final ToolCallback[] toolCallbacks;
    private final ExecutorService executor = Executors.newCachedThreadPool();

    /** requestId → 待审批的工作流上下文 */
    private final Map<String, PendingApproval> pendingApprovals = new ConcurrentHashMap<>();

    public HumanInTheLoopService(ChatClient.Builder chatClientBuilder,
                                 ToolCallback[] toolCallbacks) {
        this.chatClient = chatClientBuilder.build();
        this.toolCallbacks = toolCallbacks;
        log.info("HumanInTheLoopService initialized");
    }

    /**
     * 提交大额退款申请，运行至人工审批环节暂停。
     */
    public Map<String, Object> submitRefund(String orderId, String reason, double amount) {
        String requestId = "REQ-" + UUID.randomUUID().toString().substring(0, 8);
        log.info("submitRefund: requestId={}, orderId={}, reason={}, amount={}", requestId, orderId, reason, amount);

        // Step 1: PreCheck Agent — 前置检查
        String preCheckResult = callPreCheckAgent(orderId, reason, amount);
        log.info("PreCheck result: {}", preCheckResult);

        // 创建待审批上下文
        CountDownLatch latch = new CountDownLatch(1);
        PendingApproval pending = new PendingApproval(requestId, orderId, reason, amount, preCheckResult, latch);
        pendingApprovals.put(requestId, pending);

        // 后台线程等待审批后执行
        Future<String> future = executor.submit(() -> {
            try {
                // 阻塞等待人工审批
                log.info("[HITL] Workflow paused, waiting for approval: requestId={}", requestId);
                boolean approved = latch.await(300, TimeUnit.SECONDS);
                if (!approved) {
                    return "审批超时，工作流已终止";
                }

                String decision = pending.getDecision();
                log.info("[HITL] Approval received: requestId={}, decision={}", requestId, decision);

                if (decision.startsWith("APPROVED")) {
                    // Step 2: Execute Agent — 执行退款
                    return callExecuteAgent(orderId, reason, amount, decision);
                } else {
                    return "退款申请已被拒绝。审批意见: " + decision;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return "工作流被中断";
            } finally {
                pendingApprovals.remove(requestId);
            }
        });
        pending.setFuture(future);

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
     */
    public Map<String, Object> approve(String requestId, String decision, String comment) {
        log.info("approve: requestId={}, decision={}, comment={}", requestId, decision, comment);

        PendingApproval pending = pendingApprovals.get(requestId);
        if (pending == null) {
            return Map.of("requestId", requestId, "status", "NOT_FOUND",
                    "message", "未找到对应的待审批请求，可能已处理完成或不存在");
        }

        String approvalText = decision + (comment == null || comment.isBlank() ? "" : " - " + comment);
        pending.setDecision(approvalText);
        pending.getLatch().countDown();

        // 等待工作流完成
        try {
            String result = pending.getFuture().get(120, TimeUnit.SECONDS);
            return Map.of(
                "requestId", requestId,
                "decision", decision,
                "status", "COMPLETED",
                "executionResult", result
            );
        } catch (Exception e) {
            log.error("approve: workflow execution failed for requestId={}", requestId, e);
            return Map.of("requestId", requestId, "status", "ERROR",
                    "message", "工作流恢复执行失败: " + e.getMessage());
        }
    }

    private String callPreCheckAgent(String orderId, String reason, double amount) {
        String systemPrompt = """
            你是电商售后前置检查专员。请对大额退款申请进行前置检查。
            
            请调用工具查询订单和用户信息，然后评估：
            1. 订单是否有效且状态允许退款
            2. 申请退款金额是否与订单金额匹配
            3. 用户信用状况和历史退款记录
            4. 是否存在欺诈风险
            
            请返回前置检查结果，格式：
            检查状态: [通过/需关注/异常]
            订单验证: [有效/无效]
            金额核实: [匹配/不匹配]
            风险评估: [低/中/高]
            建议: [同意进入审批/需要额外材料/建议拒绝]
            """;

        return chatClient.prompt()
                .system(systemPrompt)
                .user("订单ID: %s\n退款原因: %s\n申请退款金额: %.2f".formatted(orderId, reason, amount))
                .tools(toolCallbacks)
                .call()
                .content();
    }

    private String callExecuteAgent(String orderId, String reason, double amount, String approvalDecision) {
        String systemPrompt = """
            你是电商售后执行专员。大额退款已通过人工审批，请执行退款操作。
            
            请调用工具查询订单信息确认详情，然后：
            1. 确认退款金额和方式
            2. 模拟执行退款操作
            3. 生成退款确认通知
            
            请返回执行结果，格式：
            执行状态: [成功/失败]
            退款金额: [实际退款金额]
            退款方式: [原路退回/其他]
            预计到账: [时间]
            用户通知: [退款确认通知文案]
            """;

        return chatClient.prompt()
                .system(systemPrompt)
                .user("订单ID: %s\n退款原因: %s\n退款金额: %.2f\n审批决定: %s"
                        .formatted(orderId, reason, amount, approvalDecision))
                .tools(toolCallbacks)
                .call()
                .content();
    }

    /**
     * 待审批的工作流上下文
     */
    static class PendingApproval {
        private final String requestId;
        private final String orderId;
        private final String reason;
        private final double amount;
        private final String preCheckResult;
        private final CountDownLatch latch;
        private volatile String decision;
        private volatile Future<String> future;

        PendingApproval(String requestId, String orderId, String reason, double amount,
                        String preCheckResult, CountDownLatch latch) {
            this.requestId = requestId;
            this.orderId = orderId;
            this.reason = reason;
            this.amount = amount;
            this.preCheckResult = preCheckResult;
            this.latch = latch;
        }

        String getDecision() { return decision; }
        void setDecision(String decision) { this.decision = decision; }
        CountDownLatch getLatch() { return latch; }
        Future<String> getFuture() { return future; }
        void setFuture(Future<String> future) { this.future = future; }
    }
}
