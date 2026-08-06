package com.example.agentic.planner;

/**
 * Planner 规划-执行工作流接口。
 * 
 * <p>与 Supervisor 的运行时动态决策不同，Planner 采用"先规划，后执行"的两阶段范式：
 * <ol>
 *   <li>PlanAgent 分析批量工单，生成结构化 JSON 执行计划</li>
 *   <li>ExecuteAgent 按计划逐步处理，ProgressAgent 跟踪进度</li>
 * </ol>
 * 
 * <p>区别：
 * <table>
 *   <tr><th>维度</th><th>Supervisor</th><th>Planner</th></tr>
 *   <tr><td>决策方式</td><td>运行时动态（走一步看一步）</td><td>事先生成完整计划（白盒可审查）</td></tr>
 *   <tr><td>适用场景</td><td>单任务、路径不确定</td><td>批量任务、需要审批计划</td></tr>
 * </table>
 */
public interface PlannerWorkflow {

    /**
     * 接收批量工单 JSON，规划并逐步执行处理。
     * 
     * @param ticketsJson 批量工单 JSON，格式：
     *                    {"tickets": [{"orderId":"ORD-001","issue":"..."}, ...]}
     * @return 完整的执行报告（含计划 + 每个工单的处理结果）
     */
    String handleBatch(String ticketsJson);
}
