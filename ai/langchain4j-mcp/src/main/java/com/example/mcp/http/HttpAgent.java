package com.example.mcp.http;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * AI Service 接口：使用 HTTP/SSE 传输的 MCP 工具。
 *
 * 这种传输方式通过 HTTP 请求与 MCP Server 通信，
 * 适用于跨网络通信的场景。
 */
public interface HttpAgent {

    @SystemMessage("""
        你是一个智能助手，通过 HTTP/SSE 传输的 MCP Server 获取外部工具能力。
        
        你可以使用的工具由远程 MCP Server 提供。
        请根据用户问题自动判断是否需要调用工具，并给出自然、友好的回答。
        """)
    @UserMessage("{{question}}")
    String ask(String question);
}
