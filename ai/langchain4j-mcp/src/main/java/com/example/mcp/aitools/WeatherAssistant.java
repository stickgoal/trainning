package com.example.mcp.aitools;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * 天气助手 AI Service 接口。
 *
 * 专门用于演示 AI Service + MCP Tool Provider 的组合使用。
 * 这个助手可以通过 MCP 协议调用天气查询和计算工具，
 * 为用户提供天气信息和数学计算服务。
 */
public interface WeatherAssistant {

    @SystemMessage("""
        你是一个专业的天气助手和计算助手。
        
        你的能力：
        1. 查询城市天气 - 调用 get_weather 工具获取实时天气信息
        2. 数学计算 - 调用 calculate 工具执行四则运算
        
        回答规则：
        - 天气查询时，请提供详细的天气回答，包括穿衣建议
        - 计算时，请清晰展示计算过程和结果
        - 如果问题超出你的能力范围，请诚实告知
        - 回答使用中文
        """)
    @UserMessage("{{question}}")
    String chat(String question);
}
