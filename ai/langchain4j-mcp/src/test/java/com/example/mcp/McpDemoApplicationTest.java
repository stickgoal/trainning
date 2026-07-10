package com.example.mcp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Spring Boot 上下文加载测试。
 *
 * <p>由于 MCP 配置已具备弹性（MCP Server 不可达时自动降级为空工具 Provider），
 * 这里直接加载完整应用上下文即可，无需排除任何配置。
 * 若 stdio 的 Node.js MCP Server 可用，则天气/计算工具会被注册；
 * 远程 HTTP/SSE、Streamable HTTP 服务器未运行时则优雅降级。
 */
@SpringBootTest
class McpDemoApplicationTest {

    @Test
    void contextLoads() {
        // 验证 Spring 上下文能正常加载（含 MCP 配置弹性初始化）
    }
}
