package com.example.agentic.planner.agent;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * 规划执行 Agent：按计划逐步处理下一个 PENDING 状态的工单。
 * 
 * <p>与 humanintheloop.agent.ExecuteAgent 职责不同：
 * <ul>
 *   <li>humanintheloop: 大额退款审批通过后的单一退款执行</li>
 *   <li>planner: 按 Planner 计划逐步处理批量工单中的每一个</li>
 * </ul>
 * 
 * <p>每轮迭代处理一个工单：
 * <ol>
 *   <li>从 plan 中找到第一个 status=PENDING 的步骤</li>
 *   <li>调用工具查询该工单的详细信息</li>
 *   <li>执行处理策略，输出处理结果</li>
 *   <li>将该步骤的 status 更新为 COMPLETED</li>
 * </ol>
 * 
 * <p>通过 @V("plan") 读取当前计划（每轮覆写更新），通过 summarizedContext = {"progressResult"} 接收上一轮进度反馈。
 * 关键在于：plan 是跨迭代累积更新的可变状态，已处理的步骤 status=COMPLETED，不会被重置。
 */
public interface PlannerExecuteAgent {

    @UserMessage("""
        你是电商售后工单执行专员。请根据执行计划处理下一个待执行的工单。
        
        ‼️ 当前执行计划（完整JSON，已包含已完成步骤的状态）:
        {{plan}}
        
        请执行以下步骤：
        
        1. 从计划的 steps 数组中，找到第一个 status 为 PENDING 的步骤（注意：已完成的步骤 status 已是 COMPLETED，不要再处理）
        2. 对该工单调用 queryOrder 和 queryUserByOrder 获取最新信息
        3. 根据该步骤的策略（strategy 字段），调用 queryProduct 获取商品信息
        4. 执行处理操作，生成处理结果
        
        如果你收到的计划中所有步骤都已 COMPLETED，说明全部处理完成，请原样输出该计划，不要新增步骤。
        如果有上一轮的进度反馈，请参考反馈中的进度信息，继续处理下一个 PENDING 工单。
        
        请在输出时：
        - 明确指出你处理了哪个工单（orderId）
        - 说明处理策略和理由
        - 返回完整的执行计划 JSON，将已处理步骤的 status 改为 COMPLETED
        - 在 JSON 的每个已完成步骤中添加 result 字段，记录具体的处理结果
        
        输出格式（必须输出完整的更新后的执行计划 JSON）：
        ```json
        {
          "summary": "总体分析摘要",
          "totalTickets": N,
          "steps": [
            {
              "stepId": 1,
              "orderId": "ORD-XXX",
              "priority": "...",
              "status": "COMPLETED",
              "result": "具体的处理结果描述",
              ...（保留原有字段）
            },
            {
              "stepId": 2,
              "orderId": "ORD-YYY",
              "priority": "...", 
              "status": "PENDING",
              ...（保留原有字段）
            }
          ]
        }
        ```
        
        ⚠️ 关键规则：
        - 每轮只处理一个工单（第一个 status=PENDING 的）
        - 必须输出完整的、更新后的执行计划 JSON
        - 不要跳过任何步骤，不要同时处理多个工单
        """)
    @Agent(name = "PlannerExecuteAgent",
            description = "规划执行Agent，按PlanAgent生成的计划逐步处理售后工单，每轮处理一个PENDING步骤并更新状态",
            outputKey = "plan",
            summarizedContext = {"progressResult"})
    String executeStep(@V("plan") String plan);
}
