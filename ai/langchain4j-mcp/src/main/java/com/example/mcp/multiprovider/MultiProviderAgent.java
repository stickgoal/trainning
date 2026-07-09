package com.example.mcp.multiprovider;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * AI Service 接口：使用多个 MCP Provider 提供的工具。
 *
 * 演示如何将多个 MCP Client 组合到一个 ToolProvider 中，
 * 让 LLM 可以调用来自不同 MCP Server 的工具。
 */
public interface MultiProviderAgent {

    @SystemMessage("""
        你是一个智能助手，可以同时使用来自多个 MCP Server 的工具。
        
        你的工具来源包括：
        1. stdio 传输的本地 MCP Server - 提供天气查询和计算工具
        2. HTTP 传输的远程 MCP Server - 提供其他工具
        
        请根据用户问题自动判断需要调用哪个工具，并给出自然、友好的回答。
        """)
    @UserMessage("{{question}}")
    String ask(String question);
}
