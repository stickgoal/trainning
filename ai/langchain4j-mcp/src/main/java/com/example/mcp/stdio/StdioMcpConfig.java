package com.example.mcp.stdio;

import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.stdio.StdioMcpTransport;
import dev.langchain4j.mcp.McpToolProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.List;

/**
 * stdio 传输方式 MCP 配置。
 *
 * 通过 StdioMcpTransport 启动一个 Node.js 子进程作为 MCP Server，
 * 客户端通过标准输入/输出与其通信。
 *
 * 工作流程：
 * 1. 从 classpath 解析 MCP Server 脚本的文件系统路径
 * 2. 构建 StdioMcpTransport，指定 node 启动命令
 * 3. 创建 McpClient 连接到 MCP Server
 * 4. 创建 McpToolProvider 将 MCP 工具暴露给 AI Service
 */
@Slf4j
@Configuration
public class StdioMcpConfig {

    @Value("${mcp.stdio.server-script:mcp-server/weather-mcp-server.js}")
    private String serverScriptPath;

    /**
     * 创建并配置 McpClient (stdio 传输)
     */
    @Bean(name = "stdioMcpClient")
    public McpClient stdioMcpClient() throws Exception {
        log.info("Initializing StdioMcpClient with server script: {}", serverScriptPath);

        // 1. 解析 MCP Server 脚本路径（从 classpath 到文件系统）
        String scriptPath = resolveScriptPath(serverScriptPath);
        log.info("Resolved MCP server script path: {}", scriptPath);

        // 2. 构建 stdio 传输：使用 node 启动 MCP Server
        McpTransport transport = StdioMcpTransport.builder()
                .command(List.of("node", scriptPath))
                .logEvents(true)
                .build();

        // 3. 创建 MCP Client
        McpClient client = DefaultMcpClient.builder()
                .key("stdio-weather-client")
                .transport(transport)
                .build();

        log.info("StdioMcpClient initialized successfully");
        return client;
    }

    /**
     * 创建 MCP Tool Provider，将 MCP 工具暴露给 AI Service
     */
    @Bean(name = "stdioToolProvider")
    public McpToolProvider stdioToolProvider(@org.springframework.beans.factory.annotation.Qualifier("stdioMcpClient") McpClient mcpClient) {
        return McpToolProvider.builder()
                .mcpClients(mcpClient)
                .build();
    }

    /**
     * 从 classpath 资源路径解析到文件系统绝对路径。
     * 开发环境：从 target/classes 或 src/main/resources 获取
     */
    private String resolveScriptPath(String resourcePath) throws Exception {
        // 尝试从 classpath 加载
        var classLoader = getClass().getClassLoader();
        var resource = classLoader.getResource(resourcePath);

        if (resource != null) {
            File file = new File(resource.toURI());
            if (file.exists()) {
                return file.getAbsolutePath();
            }
        }

        // 回退：从 src/main/resources 获取（开发时使用）
        String devPath = "src/main/resources/" + resourcePath;
        File devFile = new File(devPath);
        if (devFile.exists()) {
            return devFile.getAbsolutePath();
        }

        throw new IllegalStateException("MCP server script not found: " + resourcePath
                + ". Please ensure the file exists in src/main/resources/" + resourcePath
                + " and run 'npm install' in that directory first.");
    }
}
