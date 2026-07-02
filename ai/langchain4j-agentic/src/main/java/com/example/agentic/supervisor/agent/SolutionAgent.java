package com.example.agentic.supervisor.agent;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * 方案制定 Agent：综合情绪和事实，制定售后处理方案。
 * Supervisor 动态调度，通常在事实调查后执行。
 */
public interface SolutionAgent {

    @UserMessage("""
        你是电商售后方案制定专家。请根据情绪分析结果和事实调查结果，制定处理方案。
        
        订单ID: {{orderId}}
        用户投诉: {{complaint}}
        
        情绪分析结果:
        {{emotionResult}}
        
        事实调查结果:
        {{factResult}}
        
        请制定综合处理方案：
        
        1. 根据情绪强度决定处理优先级
        2. 根据事实判断责任归属
        3. 制定具体处理方案（退款/换货/补偿/道歉等组合）
        4. 评估方案成本和对用户满意度的影响
        
        请返回处理方案，格式如下：
        处理优先级: [紧急/高/中/低]
        责任归属: [商家责任/物流责任/用户责任/共同责任]
        处理方案: [具体方案描述]
        补偿措施: [优惠券/积分/赠品等]
        预估成本: [金额]
        用户满意度预估: [高/中/低]
        """)
    @Agent(name = "SolutionAgent",
            description = "方案制定Agent，综合情绪和事实制定售后处理方案",
            outputKey = "solutionResult")
    String makeSolution(
        @V("orderId") String orderId,
        @V("complaint") String complaint,
        @V("emotionResult") String emotionResult,
        @V("factResult") String factResult
    );
}
