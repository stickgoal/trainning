package com.example.mcp.multiprovider;

import com.example.mcp.common.FallbackToolProvider;
import com.example.mcp.common.McpResourceResolver;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.http.HttpMcpTransport;
import dev.langchain4j.mcp.client.transport.stdio.StdioMcpTransport;
import dev.langchain4j.service.tool.ToolProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * 多 MCP Provider 配置。
 *
 * <p>将多个 McpClient 组合到一个 McpToolProvider 中，
 * 让 AI Service 可以同时使用来自不同 MCP Server 的工具。
 *
 * <p>本演示组合 stdio（本地天气/计算 Server）与 http（远程 Server）两种传输。
 * 采用弹性策略：任何一个 Server 连接失败都不会导致整体失败——
 * 仅把可用的 Client 加入 Provider（等价于 failIfOneServerFails(false) 的运行时版本）。
 * 若所有 Server 都不可用，则返回 {@link FallbackToolProvider}。
 */
@Slf4j
@Configuration
public class MultiProviderConfig {

    @Value("${mcp.stdio.server-script:mcp-server/weather-mcp-server.js}")
    private String serverScriptPath;

    @Value("${mcp.http.sse-url:http://localhost:3001/sse}")
    private String sseUrl;

    /**
     * 创建组合多个 MCP Client 的 Tool Provider（返回 ToolProvider 接口）。
     */
    @Bean(name = "multiToolProvider")
    public ToolProvider multiToolProvider() {
        List<McpClient> clients = new ArrayList<>();

        // 1) stdio 传输：本地 Node.js MCP Server（项目中已内置，通常可用）
        try {
            String scriptPath = McpResourceResolver.resolve(serverScriptPath);
            McpTransport stdioTransport = StdioMcpTransport.builder()
                    .command(List.of("node", scriptPath))
                    .logEvents(true)
                    .build();
            McpClient stdioClient = DefaultMcpClient.builder()
                    .key("multi-stdio-client")
                    .transport(stdioTransport)
                    .build();
            clients.add(stdioClient);
            log.info("MultiProvider: stdio client added");
        } catch (Exception e) {
            log.warn("MultiProvider: stdio client unavailable, skipping. "
                    + "Is Node.js installed and 'npm install' run in mcp-server/?", e);
        }

        // 2) HTTP/SSE 传输：远程 Server（项目未内置，通常不可用，仅作组合演示）
        try {
            McpTransport httpTransport = HttpMcpTransport.builder()
                    .sseUrl(sseUrl)
                    .logRequests(true)
                    .logResponses(true)
                    .build();
            McpClient httpClient = DefaultMcpClient.builder()
                    .key("multi-http-client")
                    .transport(httpTransport)
                    .build();
            clients.add(httpClient);
            log.info("MultiProvider: http client added");
        } catch (Exception e) {
            log.warn("MultiProvider: http client unavailable (no built-in HTTP/SSE server at {}), "
                    + "skipping. The provider will still work with the stdio tools.", sseUrl);
        }

        if (clients.isEmpty()) {
            log.warn("MultiProvider: no MCP client could be initialized.");
            return new FallbackToolProvider("多 Provider 演示中，stdio 与 http 两个 MCP Server 均不可用，"
                    + "请检查 Node.js / npm 安装以及远程 Server 是否在 " + sseUrl + " 运行。");
        }

        log.info("Building multi-provider ToolProvider with {} client(s)", clients.size());
        return McpToolProvider.builder()
                .mcpClients(clients)
                .failIfOneServerFails(false)
                .build();
    }
}
