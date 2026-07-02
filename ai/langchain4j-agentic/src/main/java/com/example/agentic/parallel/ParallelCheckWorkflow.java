package com.example.agentic.parallel;

/**
 * Parallel 并行核验工作流接口。
 * 退款申请并行核验三个维度，汇总后给出综合评估。
 */
public interface ParallelCheckWorkflow {

    /**
     * 并行核验退款申请
     * @param orderId 订单ID
     * @param reason  退款原因
     * @return 综合核验结果
     */
    String parallelCheck(String orderId, String reason);
}
