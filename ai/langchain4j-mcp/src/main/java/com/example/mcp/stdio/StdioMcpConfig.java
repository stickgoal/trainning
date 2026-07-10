package com.example.mcp.stdio;

import com.example.mcp.common.FallbackToolProvider;
import com.example.mcp.common.McpResourceResolver;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.stdio.StdioMcpTransport;
import dev.langchain4j.service.tool.ToolProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * stdio 传输方式 MCP 配置。
 *
 * <p>通过 StdioMcpTransport 启动一个 Node.js 子进程作为 MCP Server，
 * 客户端通过标准输入/输出与其通信。
 *
 * <p>配置以弹性方式构建：若 Node.js 未安装或脚本缺失导致启动失败，
 * 不会让整个应用崩溃，而是返回一个 {@link FallbackToolProvider}（无工具），
 * 相关端点会向用户说明原因。
 */
@Slf4j
@Configuration
public class StdioMcpConfig {

    @Value("${mcp.stdio.server-script:mcp-server/weather-mcp-server.js}")
    private String serverScriptPath;

    /**
     * 创建 MCP Tool Provider（stdio 传输），将 MCP 工具暴露给 AI Service。
     *
     * <p>返回类型为 {@link ToolProvider} 接口：连接成功时返回真正的
     * {@link McpToolProvider}，失败时返回 {@link FallbackToolProvider}，
     * 从而保证 Spring 上下文一定能初始化成功。
     */
    @Bean(name = "stdioToolProvider")
    public ToolProvider stdioToolProvider() {
        try {
            String scriptPath = McpResourceResolver.resolve(serverScriptPath);
            log.info("Initializing StdioMcpClient with server script: {}", scriptPath);

            McpTransport transport = StdioMcpTransport.builder()
                    .command(List.of("node", scriptPath))
                    .logEvents(true)
                    .build();

            McpClient client = DefaultMcpClient.builder()
                    .key("stdio-weather-client")
                    .transport(transport)
                    .build();

            log.info("StdioMcpClient initialized successfully");
            return McpToolProvider.builder()
                    .mcpClients(client)
                    .build();
        } catch (Exception e) {
            log.error("Failed to initialize stdio MCP client. Is Node.js installed and "
                    + "'npm install' executed in the mcp-server directory? Falling back to no tools.", e);
            return new FallbackToolProvider("stdio MCP Server 启动失败：请确认已安装 Node.js，"
                    + "并在 mcp-server 目录执行过 npm install。脚本路径: " + serverScriptPath);
        }
    }
}
