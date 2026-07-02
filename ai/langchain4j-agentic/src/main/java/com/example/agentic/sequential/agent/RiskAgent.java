package com.example.agentic.sequential.agent;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * 风控检查 Agent：评估退款申请的风险等级。
 * 输出存入 AgenticScope 的 "riskResult" 变量，供后续 FinanceAgent 读取。
 */
public interface RiskAgent {

    @UserMessage("""
        你是电商售后风控专员。请根据以下退款申请信息进行风控评估。
        
        订单ID: {{orderId}}
        退款原因: {{reason}}
        
        请先调用工具查询订单和用户信息，然后根据以下规则评估风险：
        
        1. 高风险(HIGH)：用户历史退款次数≥3次、订单金额>500且退款原因为"不喜欢了"
        2. 中风险(MEDIUM)：用户历史退款次数1-2次、订单已签收且退款原因为个人原因
        3. 低风险(LOW)：首次退款、质量问题、VIP用户
        
        请返回评估结果，格式如下：
        风险等级: [HIGH/MEDIUM/LOW]
        风险评分: [0-100的数字，越高越危险]
        风险标签: [欺诈嫌疑/高频退款/正常退款/质量问题]
        风控建议: [对下一步处理的建议]
        """)
    @Agent(name = "RiskAgent",
            description = "风控检查Agent，评估退款申请的风险等级",
            outputKey = "riskResult")
    String assessRisk(@V("orderId") String orderId, @V("reason") String reason);
}
