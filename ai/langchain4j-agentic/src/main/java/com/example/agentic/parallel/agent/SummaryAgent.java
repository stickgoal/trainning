package com.example.agentic.parallel.agent;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * 汇总 Agent：综合三个并行核验结果，输出最终决策。
 * 注意：Parallel 模式下，三个子 Agent 的 outputKey 会各自写入 AgenticScope，
 * 汇总 Agent 通过 @V 读取这三个 key 的值。
 */
public interface SummaryAgent {

    @UserMessage("""
        你是电商售后主管。请综合以下三个并行核验结果，给出最终的退款处理决策。
        
        订单ID: {{orderId}}
        退款原因: {{reason}}
        
        订单状态核验结果:
        {{orderCheckResult}}
        
        用户信用核验结果:
        {{creditCheckResult}}
        
        库存状况核验结果:
        {{stockCheckResult}}
        
        请综合所有核验结果，做出最终决策：
        
        1. 如果所有核验都PASS → 批准退款
        2. 如果有任何FAIL → 拒绝退款并说明原因
        3. 如果有WARNING → 需要人工进一步审核
        
        请返回综合评估结果，格式如下：
        最终决策: [APPROVED/REJECTED/NEEDS_MANUAL_REVIEW]
        决策依据: [简要说明基于哪些核验结果做出的决策]
        各维度评分:
          - 订单状态: [PASS/FAIL/WARNING]
          - 用户信用: [PASS/FAIL/WARNING]
          - 库存状况: [PASS/FAIL/WARNING]
        处理建议: [具体的后续操作建议]
        """)
    @Agent(name = "SummaryAgent",
            description = "汇总Agent，综合并行核验结果做出最终决策",
            outputKey = "summaryResult")
    String summarize(
        @V("orderId") String orderId,
        @V("reason") String reason,
        @V("orderCheckResult") String orderCheckResult,
        @V("creditCheckResult") String creditCheckResult,
        @V("stockCheckResult") String stockCheckResult
    );
}
