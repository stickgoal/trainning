package com.example.mcp.common;

import dev.langchain4j.service.tool.ToolProvider;
import dev.langchain4j.service.tool.ToolProviderRequest;
import dev.langchain4j.service.tool.ToolProviderResult;

/**
 * 兜底工具提供者。
 *
 * <p>当某个 MCP Server 无法连接（例如远程 HTTP/SSE 或 Streamable HTTP 服务器未启动）时，
 * 使用本类替代真正的 {@link dev.langchain4j.mcp.McpToolProvider}，提供一个不含任何工具的
 * ToolProvider，从而保证应用可以正常启动，且对应端点能返回清晰的提示信息而不是 500 错误。
 */
public class FallbackToolProvider implements ToolProvider {

    private final String reason;

    public FallbackToolProvider(String reason) {
        this.reason = reason;
    }

    /**
     * @return 工具不可用的原因（中文提示），供 Service 层在响应中回显
     */
    public String getReason() {
        return reason;
    }

    /**
     * @return 始终为 false，表示未连接任何真实的 MCP Server
     */
    public boolean isAvailable() {
        return false;
    }

    @Override
    public ToolProviderResult provideTools(ToolProviderRequest request) {
        return ToolProviderResult.builder().build();
    }
}
