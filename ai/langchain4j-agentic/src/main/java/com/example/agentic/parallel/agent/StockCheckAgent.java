package com.example.agentic.parallel.agent;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * 库存核验 Agent：并行检查商品库存情况（影响退货/换货可行性）。
 */
public interface StockCheckAgent {

    @UserMessage("""
        你是电商售后库存核验专员。请核验退款相关商品的库存状况。
        
        订单ID: {{orderId}}
        退款原因: {{reason}}
        
        请先调用工具查询订单中的商品信息和库存，然后核验：
        
        1. 商品当前库存是否充足？影响是否可以换货而非退款
        2. 商品是否为热销品？热销品的库存变动频繁
        3. 如果用户选择退货，商品是否可以重新入库销售？
        
        请返回核验结果，格式如下：
        核验维度: 库存状况
        核验结果: [PASS/FAIL/WARNING]
        库存状态: [充足/紧张/缺货]
        换货可行性: [可行/不可行]
        备注: [补充说明]
        """)
    @Agent(name = "StockCheckAgent",
            description = "库存核验Agent，检查商品库存影响退换货可行性",
            outputKey = "stockCheckResult")
    String checkStock(@V("orderId") String orderId, @V("reason") String reason);
}
