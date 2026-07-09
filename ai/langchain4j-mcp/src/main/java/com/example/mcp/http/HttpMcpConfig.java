package com.example.mcp.http;

import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.http.HttpMcpTransport;
import dev.langchain4j.mcp.McpToolProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * HTTP/SSE 传输方式 MCP 配置（legacy）。
 *
 * 使用 HttpMcpTransport 通过 HTTP 请求与远程 MCP Server 通信。
 * 客户端通过 SSE（Server-Sent Events）通道接收事件，
 * 通过 HTTP POST 请求发送命令。
 *
 * 注意：此传输方式已被弃用，建议使用 StreamableHttpMcpTransport 替代。
 */
@Slf4j
@Configuration
public class HttpMcpConfig {

    @Value("${mcp.http.sse-url:http://localhost:3001/sse}")
    private String sseUrl;

    /**
     * 创建并配置 McpClient (HTTP/SSE 传输)
     */
    @Bean(name = "httpMcpClient")
    public McpClient httpMcpClient() {
        log.info("Initializing HttpMcpClient with sseUrl: {}", sseUrl);

        // 1. 构建 HTTP 传输
        McpTransport transport = HttpMcpTransport.builder()
                .sseUrl(sseUrl)
                .logRequests(true)
                .logResponses(true)
                .build();

        // 2. 创建 MCP Client
        McpClient client = DefaultMcpClient.builder()
                .key("http-weather-client")
                .transport(transport)
                .build();

        log.info("HttpMcpClient initialized successfully");
        return client;
    }

    /**
     * 创建 MCP Tool Provider
     */
    @Bean(name = "httpToolProvider")
    public McpToolProvider httpToolProvider(@org.springframework.beans.factory.annotation.Qualifier("httpMcpClient") McpClient mcpClient) {
        return McpToolProvider.builder()
                .mcpClients(mcpClient)
                .build();
    }
}
