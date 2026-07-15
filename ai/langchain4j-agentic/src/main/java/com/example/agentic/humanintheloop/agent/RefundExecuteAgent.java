package com.example.agentic.humanintheloop.agent;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.V;

/**
 * 退款执行 Agent（非 AI，仅做演示）。
 *
 * <p>它读取 AgenticScope 中的 {@code managerApproval}（即人工审批结论）。
 * 由于该值在审批完成前是一个 {@code PendingResponse}，<b>读取时会阻塞</b>，
 * 这正是 HumanInTheLoop「暂停」语义的体现；一旦外部完成审批，
 * 阻塞解除，本方法继续执行并产出最终结果。</p>
 */
public class RefundExecuteAgent {

    private RefundExecuteAgent() {
    }

    @Agent(description = "根据人工审批结论执行或驳回退款", outputKey = "executionResult")
    public static String execute(@V("orderId") String orderId,
                                 @V("amount") double amount,
                                 @V("managerApproval") String approval) {
        if (approval == null || !approval.toUpperCase().contains("APPROVED")) {
            return String.format("订单 %s 退款被驳回（审批结论：%s），未执行退款。", orderId, approval);
        }
        return String.format("订单 %s 退款 ¥%.2f 已执行完成（审批结论：%s）。", orderId, amount, approval);
    }
}
