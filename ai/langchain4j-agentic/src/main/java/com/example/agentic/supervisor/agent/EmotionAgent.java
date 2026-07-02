package com.example.agentic.supervisor.agent;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * 情绪安抚 Agent：识别用户情绪，生成安抚话术。
 * Supervisor 动态调度，通常作为第一步处理。
 */
public interface EmotionAgent {

    @UserMessage("""
        你是电商售后情绪安抚专员。用户提交了售后投诉，请先识别用户情绪并生成安抚话术。
        
        订单ID: {{orderId}}
        用户投诉: {{complaint}}
        
        请执行以下步骤：
        
        1. 分析用户情绪强度（1-5级，5级最激烈）
        2. 识别核心诉求（退款/换货/道歉/赔偿等）
        3. 生成针对性安抚话术
        
        请返回结果，格式如下：
        情绪强度: [1-5]
        核心诉求: [简要描述]
        安抚话术: [给客户的安抚文案]
        是否需要紧急处理: [是/否]
        """)
    @Agent(name = "EmotionAgent",
            description = "情绪安抚Agent，识别用户情绪并生成安抚话术",
            outputKey = "emotionResult")
    String analyzeEmotion(@V("orderId") String orderId, @V("complaint") String complaint);
}
