package com.example.agentic.supervisor.agent;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * 事实调查 Agent：查询订单和用户信息，梳理事实。
 * Supervisor 动态调度，通常在情绪安抚后执行。
 */
public interface FactAgent {

    @UserMessage("""
        你是电商售后事实调查专员。请调查该售后投诉的背景事实。
        
        订单ID: {{orderId}}
        用户投诉: {{complaint}}
        
        请调用工具查询以下信息：
        
        1. 订单详情（商品、金额、状态、下单时间）
        2. 用户信息（VIP等级、历史订单、历史退款）
        3. 商品信息（库存、分类）
        
        请返回调查结果，格式如下：
        订单状态: [具体状态]
        商品信息: [名称、金额等]
        用户画像: [VIP等级、历史行为]
        事实摘要: [一段话概述事实]
        关键发现: [影响处理决策的关键信息]
        """)
    @Agent(name = "FactAgent",
            description = "事实调查Agent，查询订单用户信息梳理事实",
            outputKey = "factResult")
    String investigateFact(@V("orderId") String orderId, @V("complaint") String complaint);
}
