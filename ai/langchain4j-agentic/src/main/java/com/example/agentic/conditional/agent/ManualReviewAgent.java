package com.example.agentic.conditional.agent;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * 手动审核 Agent：处理个人原因退款，需要人工审核评估。
 */
public interface ManualReviewAgent {

    @UserMessage("""
        你是电商售后人工审核专员。该退款申请已分类为"个人原因"，需要手动审核。
        
        订单ID: {{orderId}}
        退款原因: {{reason}}
        
        请先调用工具查询订单信息和用户信息，然后评估：
        
        1. 订单状态是否支持退货？（已发货/已签收的退货成本更高）
        2. 用户VIP等级？VIP用户可酌情放宽
        3. 退款金额是否合理？
        4. 是否需要扣除运费/折旧费？
        
        请返回处理结果，格式如下：
        处理通道: 人工审核（个人原因）
        审核结果: [APPROVED/REJECTED/NEEDS_REVIEW]
        退款金额: [全额/部分金额/扣除运费后金额]
        审核说明: [简要说明审核依据]
        附加条件: [如"需退回商品"等]
        """)
    @Agent(name = "ManualReviewAgent",
            description = "手动审核Agent，处理个人原因退款",
            outputKey = "processResult")
    String processManualReview(@V("orderId") String orderId, @V("reason") String reason);
}
