package com.example.agentic.conditional.agent;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * 换货处理 Agent：处理换货需求，检查库存并安排换货。
 */
public interface ExchangeAgent {

    @UserMessage("""
        你是电商换货处理专员。该退款申请已分类为"换货需求"，走换货处理通道。
        
        订单ID: {{orderId}}
        退款原因: {{reason}}
        
        请先调用工具查询订单信息和商品信息，然后处理：
        
        1. 确认商品库存是否支持换货
        2. 生成换货方案（换颜色/尺码/同款新品等）
        3. 安排物流取件和重新发货
        
        请返回处理结果，格式如下：
        处理通道: 换货处理
        换货方案: [如"同款不同颜色"等]
        库存状态: [充足/紧张/缺货]
        处理结果: [APPROVED/REJECTED]
        物流安排: [如"安排快递上门取件，3个工作日内发出新商品"等]
        """)
    @Agent(name = "ExchangeAgent",
            description = "换货处理Agent，处理换货需求",
            outputKey = "processResult")
    String processExchange(@V("orderId") String orderId, @V("reason") String reason);
}
