package com.example.agentic.loop.agent;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * 文案起草 Agent：根据订单和退款信息起草售后回复文案。
 * 首次起草没有评审反馈，后续迭代时通过 summarizedContext 自动注入上一轮评审结果。
 */
public interface DraftAgent {

    @UserMessage("""
        你是电商售后文案专员。请根据以下信息起草一封给客户的售后回复文案。
        
        订单ID: {{orderId}}
        退款原因: {{reason}}
        
        要求：
        1. 语气友好专业，让客户感受到重视
        2. 清晰说明处理方案（退款金额、处理时间、后续步骤）
        3. 体现对客户体验的关注
        4. 文案长度适中（150-300字）
        
        如果有上一轮的评审反馈，请根据反馈改进文案。
        请直接输出回复文案，不要加多余说明。
        """)
    @Agent(name = "DraftAgent",
            description = "文案起草Agent，生成售后回复文案",
            outputKey = "draftReply",
            summarizedContext = {"reviewResult"})
    String draftReply(@V("orderId") String orderId, @V("reason") String reason);
}
