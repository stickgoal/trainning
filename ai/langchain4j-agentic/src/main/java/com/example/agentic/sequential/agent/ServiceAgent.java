package com.example.agentic.sequential.agent;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * 客服确认 Agent：综合风控和财务结果，生成最终处理结果。
 * 通过 @V 读取前两步结果，自身输出存入 "finalResult"。
 */
public interface ServiceAgent {

    @UserMessage("""
        你是电商售后客服主管。请根据风控评估和财务核算结果，生成最终处理方案。
        
        订单ID: {{orderId}}
        退款原因: {{reason}}
        风控结果: {{riskResult}}
        财务核算结果: {{financeResult}}
        
        请综合以上信息，做出最终处理决定：
        
        1. 如果风控为LOW且财务全额退款 → 直接批准全额退款
        2. 如果风控为MEDIUM → 批准部分退款，同时给用户发送补偿优惠券
        3. 如果风控为HIGH → 需要进一步审核，暂时拒绝退款但保留申诉通道
        
        请返回最终结果，格式如下：
        处理结果: [APPROVED/PARTIAL_REFUND/REJECTED]
        最终退款金额: [数字]
        处理说明: [详细说明处理决定的原因]
        用户通知: [发给用户的退款通知文案，语气友好专业]
        """)
    @Agent(name = "ServiceAgent",
            description = "客服确认Agent，综合评估后生成最终处理结果",
            outputKey = "finalResult")
    String finalizeResult(
        @V("orderId") String orderId,
        @V("reason") String reason,
        @V("riskResult") String riskResult,
        @V("financeResult") String financeResult
    );
}
