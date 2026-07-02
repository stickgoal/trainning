package com.example.agentic.humanintheloop;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.V;

/**
 * HumanInTheLoop 人工审批工作流接口。
 * 
 * 流程：PreCheckAgent（前置检查）→ HumanInTheLoop（暂停等待人工审批）→ ExecuteAgent（执行）
 * 
 * {@code requestId} 通过 {@link MemoryId} 绑定到唯一的 AgenticScope，
 * 使得外部系统（REST 接口）能够在工作流暂停期间定位到对应会话并注入人工审批结果。
 */
public interface HumanApprovalWorkflow {

    /**
     * 处理需人工审批的大额退款。
     * 该方法在后台线程执行时会在人工审批环节阻塞，直到外部调用
     * {@code AgenticScope.completePendingResponse("managerApproval", ...)} 注入审批结果。
     *
     * @param requestId 审批请求ID（同时作为 AgenticScope 的 memoryId）
     * @param orderId   订单ID
     * @param reason    退款原因
     * @param amount    申请退款金额
     * @return 最终执行结果
     */
    String process(@MemoryId String requestId,
                   @V("orderId") String orderId,
                   @V("reason") String reason,
                   @V("amount") double amount);
}
