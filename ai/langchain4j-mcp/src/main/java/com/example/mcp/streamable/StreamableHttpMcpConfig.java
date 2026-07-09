package com.example.mcp.streamable;

import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.http.StreamableHttpMcpTransport;
import dev.langchain4j.mcp.McpToolProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Streamable HTTP 传输方式 MCP 配置。
 *
 * Streamable HTTP 是 MCP 协议 2025-06-18 规范定义的标准传输方式：
 * - 客户端发送 HTTP 请求
 * - 服务器返回常规 HTTP 响应，或打开 SSE 流发送多个响应
 * - 可选启用 subsidiaryChannel 接收服务器主动推送
 *
 * 这是最推荐的 HTTP 传输方式。
 */
@Slf4j
@Configuration
public class StreamableHttpMcpConfig {

    @Value("${mcp.streamable.url:http://localhost:3002/mcp}")
    private String mcpUrl;

    /**
     * 创建并配置 McpClient (Streamable HTTP 传输)
     */
    @Bean(name = "streamableMcpClient")
    public McpClient streamableMcpClient() {
        log.info("Initializing StreamableMcpClient with url: {}", mcpUrl);

        // 1. 构建 Streamable HTTP 传输
        McpTransport transport = StreamableHttpMcpTransport.builder()
                .url(mcpUrl)
                .logRequests(true)
                .logResponses(true)
                .build();

        // 2. 创建 MCP Client
        McpClient client = DefaultMcpClient.builder()
                .key("streamable-weather-client")
                .transport(transport)
                .build();

        log.info("StreamableMcpClient initialized successfully");
        return client;
    }

    /**
     * 创建 MCP Tool Provider
     */
    @Bean(name = "streamableToolProvider")
    public McpToolProvider streamableToolProvider(@org.springframework.beans.factory.annotation.Qualifier("streamableMcpClient") McpClient mcpClient) {
        return McpToolProvider.builder()
                .mcpClients(mcpClient)
                .build();
    }
}
