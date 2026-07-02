package com.example.agentic.conditional.agent;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * 快速退款 Agent：处理质量问题退款，自动批准全额退款。
 */
public interface AutoRefundAgent {

    @UserMessage("""
        你是电商售后快速退款处理专员。该退款申请已分类为"商品质量问题"，走快速退款通道。
        
        订单ID: {{orderId}}
        退款原因: {{reason}}
        
        请先调用工具查询订单信息，然后处理：
        
        1. 质量问题默认全额退款
        2. 生成退款处理结果
        
        请返回处理结果，格式如下：
        处理通道: 快速退款（质量问题）
        退款金额: [全额/部分金额]
        处理结果: [APPROVED/REJECTED]
        处理说明: [简要说明]
        后续步骤: [如"1-3个工作日原路退回"等]
        """)
    @Agent(name = "AutoRefundAgent",
            description = "快速退款Agent，处理质量问题退款",
            outputKey = "processResult")
    String processAutoRefund(@V("orderId") String orderId, @V("reason") String reason);
}
