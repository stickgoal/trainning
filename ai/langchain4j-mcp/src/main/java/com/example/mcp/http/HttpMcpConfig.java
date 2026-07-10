package com.example.mcp.http;

import com.example.mcp.common.FallbackToolProvider;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.http.HttpMcpTransport;
import dev.langchain4j.service.tool.ToolProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * HTTP/SSE 传输方式 MCP 配置（legacy）。
 *
 * <p>使用 HttpMcpTransport 通过 HTTP 请求与远程 MCP Server 通信。
 * 该传输方式已被弃用，建议使用 StreamableHttpMcpTransport 替代。
 *
 * <p>本演示仅展示配置方式：项目中并未内置对应的 HTTP/SSE MCP Server，
 * 因此连接通常会失败。配置以弹性方式构建——连接失败时返回
 * {@link FallbackToolProvider}，保证应用可启动，相关端点给出明确提示。
 */
@Slf4j
@Configuration
public class HttpMcpConfig {

    @Value("${mcp.http.sse-url:http://localhost:3001/sse}")
    private String sseUrl;

    /**
     * 创建 MCP Tool Provider（HTTP/SSE 传输）。
     * 连接失败时不抛异常，而是返回兜底 Provider。
     */
    @Bean(name = "httpToolProvider")
    public ToolProvider httpToolProvider() {
        try {
            log.info("Initializing HttpMcpClient with sseUrl: {}", sseUrl);

            McpTransport transport = HttpMcpTransport.builder()
                    .sseUrl(sseUrl)
                    .logRequests(true)
                    .logResponses(true)
                    .build();

            McpClient client = DefaultMcpClient.builder()
                    .key("http-weather-client")
                    .transport(transport)
                    .build();

            log.info("HttpMcpClient initialized successfully");
            return McpToolProvider.builder()
                    .mcpClients(client)
                    .build();
        } catch (Exception e) {
            log.warn("HTTP/SSE MCP server not reachable at {}. "
                    + "This transport is a configuration demo only (no built-in server). "
                    + "Falling back to no tools for this transport.", sseUrl);
            return new FallbackToolProvider("HTTP/SSE MCP Server 未运行（预期地址 " + sseUrl
                    + "）。该传输仅为配置演示，请自行启动对应的 MCP Server 后重试。");
        }
    }
}
