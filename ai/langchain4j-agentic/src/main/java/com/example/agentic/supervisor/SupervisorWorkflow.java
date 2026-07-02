package com.example.agentic.supervisor;

/**
 * Supervisor 主管调度工作流接口。
 * 由 Supervisor Agent 动态编排子 Agent 处理复杂售后诉求。
 */
public interface SupervisorWorkflow {

    /**
     * 处理复杂售后诉求
     * @param orderId 订单ID
     * @param complaint 用户投诉内容
     * @return 最终处理结果
     */
    String handleComplaint(String orderId, String complaint);
}
