package com.example.agentic.planner.agent;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * 规划 Agent：分析批量售后工单，生成结构化 JSON 执行计划。
 * 
 * <p>输出一个完整的执行计划（plan），包含：
 * <ul>
 *   <li>总体分析摘要</li>
 *   <li>每个工单的处理步骤（含优先级、处理策略、预估时间）</li>
 *   <li>推荐执行顺序（紧急优先、高价值优先）</li>
 * </ul>
 * 
 * <p>该计划存入 AgenticScope key="plan"，供后续 ExecuteAgent 逐步执行并在迭代间持续更新。
 */
public interface PlanAgent {

    @UserMessage("""
        你是电商售后工单规划专家。请根据收到的批量工单列表，制定一个结构化的执行计划。
        
        收到的工单列表（JSON格式）:
        {{ticketsJson}}
        
        请依次执行以下步骤：
        
        1. 对每个工单调用 queryOrder 和 queryUserByOrder 查询订单与用户背景
        2. 根据查询结果分析每个工单的：
           - 紧急程度（高风险用户/大额订单/投诉激烈 → 紧急）
           - 复杂度（问题类型、是否需要多方协调）
           - 预估处理时间
        3. 综合排序：紧急的排前面、高价值用户排前面
        4. 为每个工单制定处理策略（退款/换货/补偿/道歉等组合）
        
        请严格按以下 JSON 格式输出执行计划（不要包含其他文字）：
        
        ```json
        {
          "summary": "总体分析摘要",
          "totalTickets": 3,
          "createdAt": "计划创建时间",
          "steps": [
            {
              "stepId": 1,
              "orderId": "ORD-XXX",
              "userId": "USR-XXX",
              "priority": "URGENT",
              "issue": "问题描述",
              "orderStatus": "订单状态",
              "orderAmount": 299.00,
              "userVipLevel": "VIP等级",
              "userRefundHistory": "历史退款情况",
              "strategy": "处理策略描述",
              "estimatedMinutes": 5,
              "status": "PENDING"
            }
          ]
        }
        ```
        
        注意：
        - priority 取值: URGENT（紧急）、HIGH（高）、MEDIUM（中）、LOW（低）
        - status 字段初始值统一为 PENDING
        - 确保 JSON 格式合法，不需要任何转义
        """)
    @Agent(name = "PlanAgent",
            description = "规划Agent，分析批量工单并生成结构化的执行计划，确定优先级和处理策略",
            outputKey = "plan")
    String createPlan(@V("ticketsJson") String ticketsJson);
}
