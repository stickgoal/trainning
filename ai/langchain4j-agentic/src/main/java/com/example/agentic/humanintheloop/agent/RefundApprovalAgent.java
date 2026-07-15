package com.example.agentic.humanintheloop.agent;

import dev.langchain4j.agentic.declarative.HumanInTheLoop;
import dev.langchain4j.agentic.internal.PendingResponse;
import dev.langchain4j.service.V;

/**
 * 退款人工审批 Agent —— 使用 LangChain4j 官方 {@code @HumanInTheLoop} 注解标记。
 *
 * <p>关键点：被 {@code @HumanInTheLoop} 注解的静态方法返回一个 {@link PendingResponse}。
 * 工作流执行到该 Agent 时，{@code PendingResponse} 被写入 AgenticScope 的
 * {@code managerApproval}；下游 {@link RefundExecuteAgent} 读取 {@code managerApproval}
 * 时会触发 {@code PendingResponse.blockingGet()}，工作流线程随即<b>阻塞</b>，
 * 直到外部通过 {@code AgenticScope.completePendingResponse(responseId, value)} 注入审批结论。</p>
 *
 * <p>{@code PendingResponse} 的 {@code responseId} 与业务 ID(orderId) 绑定，
 * 是后续「序列化落库 → 重启后反序列化恢复」的关联键。</p>
 */
public interface RefundApprovalAgent {

    /**
     * 请求人工审批。
     *
     * @param orderId 业务订单ID（同时用于构造 responseId）
     * @param amount  退款金额（供日志/上下文）
     * @return 一个未完成的 {@link PendingResponse}，responseId = "approval:" + orderId
     */
    @HumanInTheLoop(description = "暂停工作流，等待售后主管对退款进行人工审批", outputKey = "managerApproval")
    static PendingResponse<String> requestApproval(@V("orderId") String orderId,
                                                    @V("amount") double amount) {
        String responseId = "approval:" + orderId;
        // 这里仅创建一个"待完成"的 PendingResponse 并返回；
        // 真正的"完成"由 ApprovalService 在收到人工审批后调用 completePendingResponse 完成。
        return new PendingResponse<>(responseId);
    }
}
