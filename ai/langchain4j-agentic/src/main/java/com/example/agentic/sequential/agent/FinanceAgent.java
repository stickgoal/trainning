package com.example.agentic.sequential.agent;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * 财务核算 Agent：根据风控结果计算退款金额。
 * 通过 @V("riskResult") 从 AgenticScope 读取前一步输出，
 * 自身输出存入 "financeResult"。
 */
public interface FinanceAgent {

    @UserMessage("""
        你是电商售后财务专员。请根据风控评估结果核算退款金额。
        
        订单ID: {{orderId}}
        风控结果: {{riskResult}}
        
        请先调用工具查询订单详情，然后根据以下规则核算退款：
        
        1. 风控等级为LOW：全额退款（订单总价）
        2. 风控等级为MEDIUM：退款80%，扣除20%手续费
        3. 风控等级为HIGH：退款50%，扣除50%违约金
        
        请返回核算结果，格式如下：
        订单金额: [原订单金额]
        退款金额: [实际退款金额]
        扣除金额: [扣除的金额]
        扣除原因: [手续费/违约金/无扣除]
        核算说明: [简要说明核算逻辑]
        """)
    @Agent(name = "FinanceAgent",
            description = "财务核算Agent，根据风控结果计算退款金额",
            outputKey = "financeResult")
    String calculateRefund(@V("orderId") String orderId, @V("riskResult") String riskResult);
}
