package com.example.agentic.loop;

/**
 * Loop 迭代优化工作流接口。
 * 自动生成售后回复文案，循环"起草→评审"直到质量达标。
 */
public interface LoopReplyWorkflow {

    /**
     * 迭代优化售后回复文案
     * @param orderId 订单ID
     * @param reason  退款/投诉原因
     * @return 最终优化的回复文案
     */
    String generateReply(String orderId, String reason);
}
