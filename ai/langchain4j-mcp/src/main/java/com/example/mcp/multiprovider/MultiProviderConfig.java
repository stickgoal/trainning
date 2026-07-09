package com.example.mcp.multiprovider;

import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.McpToolProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 多 MCP Provider 配置。
 *
 * 将多个 McpClient 组合到一个 McpToolProvider 中，
 * 让 AI Service 可以同时使用来自不同 MCP Server 的工具。
 *
 * 关键特性：
 * - failIfOneServerFails(false): 某个 Server 失败时继续使用其他 Server
 * - filterToolNames: 可选过滤工具名称
 * - toolNameMapper: 可选重命名工具避免冲突
 */
@Slf4j
@Configuration
public class MultiProviderConfig {

    /**
     * 创建组合多个 MCP Client 的 Tool Provider
     */
    @Bean(name = "multiToolProvider")
    public McpToolProvider multiToolProvider(
            @Qualifier("stdioMcpClient") McpClient stdioClient,
            @Qualifier("httpMcpClient") McpClient httpClient) {

        log.info("Building multi-provider ToolProvider with stdio + http clients");

        return McpToolProvider.builder()
                .mcpClients(stdioClient, httpClient)
                .failIfOneServerFails(false)  // 某个 Server 失败不影响其他
                .build();
    }
}
