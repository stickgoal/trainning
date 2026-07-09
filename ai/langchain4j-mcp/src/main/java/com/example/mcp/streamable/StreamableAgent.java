package com.example.mcp.streamable;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * AI Service 接口：使用 Streamable HTTP 传输的 MCP 工具。
 *
 * Streamable HTTP 是 MCP 协议的最新传输标准，
 * 客户端通过 HTTP 请求与服务器通信，
 * 服务器可选择打开 SSE 流以发送多个响应。
 */
public interface StreamableAgent {

    @SystemMessage("""
        你是一个智能助手，通过 Streamable HTTP 传输的 MCP Server 获取外部工具能力。
        
        你可以使用的工具由远程 MCP Server 提供。
        请根据用户问题自动判断是否需要调用工具，并给出自然、友好的回答。
        """)
    @UserMessage("{{question}}")
    String ask(String question);
}
