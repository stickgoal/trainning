package com.example.agentic.planner.agent;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * 进度检查 Agent：检查执行计划中是否所有工单都已处理完成。
 * 
 * <p>每轮迭代在 ExecuteAgent 执行后运行：
 * <ul>
 *   <li>如果还有 PENDING 状态的步骤 → 输出 CONTINUE + 下一个待处理工单的提示</li>
 *   <li>如果全部为 COMPLETED → 输出 ALL_COMPLETED + 执行总结报告</li>
 * </ul>
 * 
 * <p>通过 @V("plan") 接收 ExecuteAgent 更新后的完整计划（含已完成步骤状态）。
 */
public interface ProgressAgent {

    @UserMessage("""
        你是电商售后工单进度管理员。请检查执行计划中所有工单的处理进度。
        
        ‼️ 当前执行计划（包含已完成步骤的状态）:
        {{plan}}
        
        请做以下检查：
        
        1. 遍历 steps 数组中所有步骤的 status 字段
        2. 统计 PENDING 和 COMPLETED 的数量
        3. 计算完成百分比
        4. 如果还有 PENDING 的步骤，指出下一个待处理的工单是什么
        
        判断规则：
        - 如果所有步骤的 status 全部为 COMPLETED → 输出 ALL_COMPLETED + 生成执行总结报告
        - 如果还有步骤的 status 为 PENDING → 输出 CONTINUE + 当前进度信息
        
        如果 ALL_COMPLETED，请输出：
        ALL_COMPLETED
        完成总结:
        总工单数: [N]
        已完成: [N]
        处理结果汇总:
        1. [工单ID] - [处理结果简述]
        2. [工单ID] - [处理结果简述]
        ...
        
        如果 CONTINUE，请输出：
        CONTINUE
        进度: [已完成数]/[总数] ([百分比]%)
        下一个待处理: [orderId] - [优先级] - [问题简述]
        
        注意：开头必须输出 ALL_COMPLETED 或 CONTINUE（首行），这很重要！
        """)
    @Agent(name = "ProgressAgent",
            description = "进度检查Agent，检查计划执行进度，判断是否所有工单都已处理完成",
            outputKey = "progressResult")
    String checkProgress(@V("plan") String plan);
}
