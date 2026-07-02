package com.example.agentic.conditional.agent;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * 分类 Agent：分析退款原因，输出分类结果。
 * 分类结果写入 AgenticScope 的 refundCategory key，供后续条件路由使用。
 */
public interface ClassifyAgent {

    @UserMessage("""
        你是电商售后分类专员。请根据退款信息判断退款类别。
        
        订单ID: {{orderId}}
        退款原因: {{reason}}
        
        请先调用工具查询订单信息，然后根据退款原因和订单信息分类。
        
        分类规则：
        - QUALITY_ISSUE：商品质量问题（损坏、故障、与描述不符等）
        - PERSONAL_REASON：个人原因（不喜欢、尺寸不合、买错了等）
        - EXCHANGE_REQUEST：换货需求（颜色/尺码换货、发错货等）
        
        请只输出分类结果（一个词），格式如下：
        CATEGORY: [QUALITY_ISSUE 或 PERSONAL_REASON 或 EXCHANGE_REQUEST]
        分类依据: [简要说明]
        """)
    @Agent(name = "ClassifyAgent",
            description = "退款分类Agent，根据退款原因判断类别",
            outputKey = "refundCategory")
    String classify(@V("orderId") String orderId, @V("reason") String reason);
}
