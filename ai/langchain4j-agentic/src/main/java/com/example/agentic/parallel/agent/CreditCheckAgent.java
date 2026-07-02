package com.example.agentic.parallel.agent;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * 用户信用核验 Agent：并行检查用户信用等级和退款历史。
 */
public interface CreditCheckAgent {

    @UserMessage("""
        你是电商售后信用核验专员。请核验申请退款用户的信用状况。
        
        订单ID: {{orderId}}
        退款原因: {{reason}}
        
        请先调用工具查询订单对应的用户信息和用户历史数据，然后核验：
        
        1. 用户VIP等级？VIP/SVIP用户享有更高退款优先级
        2. 用户历史退款次数？频繁退款可能是异常行为
        3. 用户历史订单数？活跃用户更可信
        
        请返回核验结果，格式如下：
        核验维度: 用户信用
        核验结果: [PASS/FAIL/WARNING]
        信用等级: [GOOD/NORMAL/POOR]
        退款风险评估: [低/中/高]
        备注: [补充说明]
        """)
    @Agent(name = "CreditCheckAgent",
            description = "用户信用核验Agent，检查用户退款信用状况",
            outputKey = "creditCheckResult")
    String checkCredit(@V("orderId") String orderId, @V("reason") String reason);
}
