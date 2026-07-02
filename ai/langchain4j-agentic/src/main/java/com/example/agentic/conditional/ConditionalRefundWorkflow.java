package com.example.agentic.conditional;

/**
 * Conditional 条件分流工作流接口。
 * 根据退款原因分类，走不同处理路径。
 */
public interface ConditionalRefundWorkflow {

    /**
     * 条件分流处理退款
     * @param orderId 订单ID
     * @param reason  退款原因
     * @return 处理结果
     */
    String processRefund(String orderId, String reason);
}
