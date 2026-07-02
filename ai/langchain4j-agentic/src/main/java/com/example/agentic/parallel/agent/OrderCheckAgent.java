package com.example.agentic.parallel.agent;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * 订单状态核验 Agent：并行检查订单状态是否符合退款条件。
 */
public interface OrderCheckAgent {

    @UserMessage("""
        你是电商售后订单核验专员。请核验该订单的状态是否符合退款条件。
        
        订单ID: {{orderId}}
        退款原因: {{reason}}
        
        请调用工具查询订单信息，然后核验：
        
        1. 订单是否已发货/已签收？未发货的订单退款更简单
        2. 订单金额是否在合理退款范围内？
        3. 订单是否存在异常状态（如已退款、已取消）？
        
        请返回核验结果，格式如下：
        核验维度: 订单状态
        核验结果: [PASS/FAIL/WARNING]
        订单状态: [具体状态描述]
        退款可行性: [可行/不可行/需注意]
        备注: [补充说明]
        """)
    @Agent(name = "OrderCheckAgent",
            description = "订单状态核验Agent，检查订单是否符合退款条件",
            outputKey = "orderCheckResult")
    String checkOrder(@V("orderId") String orderId, @V("reason") String reason);
}
