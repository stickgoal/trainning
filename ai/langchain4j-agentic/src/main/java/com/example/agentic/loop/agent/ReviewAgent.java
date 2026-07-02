package com.example.agentic.loop.agent;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * 文案评审 Agent：评审起草的文案质量，给出评分和改进建议。
 * 如果评分达标（≥8分），输出 "APPROVED"；否则输出改进建议。
 */
public interface ReviewAgent {

    @UserMessage("""
        你是电商售后文案质量评审专家。请评审以下售后回复文案的质量。
        
        订单ID: {{orderId}}
        退款原因: {{reason}}
        待评审文案:
        {{draftReply}}
        
        请从以下维度评分（每项1-10分）：
        
        1. 专业性：文案是否专业、准确、无歧义？
        2. 友好度：语气是否温暖、让客户感到被重视？
        3. 完整性：是否包含了处理方案、时间线、后续步骤？
        4. 清晰度：信息是否清晰易懂，避免冗余？
        
        综合评分 = 四项平均分
        
        如果综合评分 ≥ 8，输出:
        APPROVED
        综合评分: [分数]
        文案已达标
        
        如果综合评分 < 8，输出:
        NEEDS_IMPROVEMENT
        综合评分: [分数]
        改进建议: [具体的改进方向，如"增加退款时间说明"、"语气更温和"等]
        """)
    @Agent(name = "ReviewAgent",
            description = "文案评审Agent，评审文案质量并给出改进建议",
            outputKey = "reviewResult")
    String reviewReply(@V("orderId") String orderId, @V("reason") String reason, @V("draftReply") String draftReply);
}
