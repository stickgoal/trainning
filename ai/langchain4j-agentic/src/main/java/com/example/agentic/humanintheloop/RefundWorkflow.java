package com.example.agentic.humanintheloop;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.V;

/**
 * 退款审批顺序工作流接口。
 *
 * <p>由 {@code AgenticServices.sequenceBuilder(RefundWorkflow.class)
 * .subAgents(RefundPreCheckAgent.class, RefundApprovalAgent.class, RefundExecuteAgent.class)
 * .build()} 装配。</p>
 *
 * <p>{@code requestId} 通过 {@link MemoryId} 绑定到唯一的 AgenticScope，
 * 使外部系统能在工作流暂停期间定位会话并注入审批结果。这里为简化「按业务ID恢复」，
 * 直接用 {@code orderId} 作为 memoryId。</p>
 */
public interface RefundWorkflow {

    /**
     * 处理需人工审批的退款。
     * 在后台线程执行时会在人工审批环节阻塞，直到外部调用
     * {@code AgenticScope.completePendingResponse("approval:" + orderId, ...)} 注入审批结果。
     *
     * @param requestId 审批请求ID（同时作为 AgenticScope 的 memoryId，这里取 orderId）
     * @param orderId   订单ID（业务ID）
     * @param reason    退款原因
     * @param amount    申请退款金额
     * @return 最终执行结果
     */
    String process(@MemoryId String requestId,
                   @V("orderId") String orderId,
                   @V("reason") String reason,
                   @V("amount") double amount);
}
