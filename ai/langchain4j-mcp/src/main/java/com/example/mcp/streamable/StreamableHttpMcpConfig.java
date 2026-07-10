package com.example.mcp.streamable;

import com.example.mcp.common.FallbackToolProvider;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.http.StreamableHttpMcpTransport;
import dev.langchain4j.service.tool.ToolProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Streamable HTTP 传输方式 MCP 配置。
 *
 * <p>Streamable HTTP 是 MCP 协议 2025-06-18 规范定义的标准传输方式，
 * 是最推荐的 HTTP 传输。本演示同样仅展示配置方式（项目未内置对应 Server），
 * 连接失败时弹性降级为 {@link FallbackToolProvider}。
 */
@Slf4j
@Configuration
public class StreamableHttpMcpConfig {

    @Value("${mcp.streamable.url:http://localhost:3002/mcp}")
    private String mcpUrl;

    /**
     * 创建 MCP Tool Provider（Streamable HTTP 传输）。
     * 连接失败时不抛异常，而是返回兜底 Provider。
     */
    @Bean(name = "streamableToolProvider")
    public ToolProvider streamableToolProvider() {
        try {
            log.info("Initializing StreamableMcpClient with url: {}", mcpUrl);

            McpTransport transport = StreamableHttpMcpTransport.builder()
                    .url(mcpUrl)
                    .logRequests(true)
                    .logResponses(true)
                    .build();

            McpClient client = DefaultMcpClient.builder()
                    .key("streamable-weather-client")
                    .transport(transport)
                    .build();

            log.info("StreamableMcpClient initialized successfully");
            return McpToolProvider.builder()
                    .mcpClients(client)
                    .build();
        } catch (Exception e) {
            log.warn("Streamable HTTP MCP server not reachable at {}. "
                    + "This transport is a configuration demo only (no built-in server). "
                    + "Falling back to no tools for this transport.", mcpUrl);
            return new FallbackToolProvider("Streamable HTTP MCP Server 未运行（预期地址 " + mcpUrl
                    + "）。该传输仅为配置演示，请自行启动对应的 MCP Server 后重试。");
        }
    }
}
