package com.example.mcp.stdio;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * AI Service 接口：使用 stdio 传输的 MCP 工具。
 *
 * 这个 AI Service 通过 MCP Tool Provider 获取由 Node.js MCP Server 提供的
 * get_weather 和 calculate 工具，用户提问时 LLM 会自动决定是否调用这些工具。
 */
public interface StdioAgent {

    @SystemMessage("""
        你是一个智能助手，可以通过 MCP 协议调用外部工具。
        
        你可以使用的工具包括：
        1. get_weather - 查询城市天气信息
        2. calculate - 执行数学计算
        
        请根据用户问题自动判断是否需要调用工具，并给出自然、友好的回答。
        """)
    @UserMessage("{{question}}")
    String ask(String question);
}
