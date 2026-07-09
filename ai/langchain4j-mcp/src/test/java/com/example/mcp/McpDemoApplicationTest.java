package com.example.mcp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Spring Boot 上下文加载测试。
 *
 * 注意：由于 MCP Client 需要实际连接 MCP Server，
 * 测试中禁用了 MCP 自动配置以避免启动失败。
 */
@SpringBootTest
@TestPropertySource(properties = {
    "spring.autoconfigure.exclude=" +
        "com.example.mcp.stdio.StdioMcpConfig," +
        "com.example.mcp.http.HttpMcpConfig," +
        "com.example.mcp.streamable.StreamableHttpMcpConfig," +
        "com.example.mcp.multiprovider.MultiProviderConfig"
})
class McpDemoApplicationTest {

    @Test
    void contextLoads() {
        // 验证 Spring 上下文能正常加载
    }
}
