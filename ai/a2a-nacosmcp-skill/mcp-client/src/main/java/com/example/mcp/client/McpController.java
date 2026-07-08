package com.example.mcp.client;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * MCP 客户端控制器
 * 功能点：通过 ChatClient + MCP 工具回调，让大模型自动发现并调用
 * Nacos 中注册的 MCP 工具（天气查询、计算器等）
 * 
 * 调用链：用户请求 → ChatClient → 大模型决策 → 调用 MCP 工具 → 返回结果
 */
@RestController
@RequestMapping("/api/mcp")
public class McpController {

    private final ChatClient chatClient;
    private final ToolCallbackProvider toolCallbackProvider;

    public McpController(ChatClient.Builder chatClientBuilder, ToolCallbackProvider toolCallbackProvider) {
        this.chatClient = chatClientBuilder.build();
        this.toolCallbackProvider = toolCallbackProvider;
    }

    /**
     * MCP 工具调用演示
     * 示例：GET /api/mcp/chat?question=查询北京的天气和空气质量
     * 大模型会自动识别需要调用 weather-mcp-service 的工具
     */
    @GetMapping("/chat")
    public String chat(@RequestParam String question) {
        return chatClient.prompt()
                .user(question)
                .tools(toolCallbackProvider.getToolCallbacks())
                .call()
                .content();
    }

    /**
     * 综合计算演示
     * 示例：GET /api/mcp/calculate?question=计算 15 的阶乘，再算 256 的平方根
     * 大模型会自动调用 calculator-mcp-service 的工具
     */
    @GetMapping("/calculate")
    public String calculate(@RequestParam String question) {
        return chatClient.prompt()
                .user(question)
                .tools(toolCallbackProvider.getToolCallbacks())
                .call()
                .content();
    }

    /**
     * 跨服务调用演示
     * 示例：GET /api/mcp/cross?question=北京今天天气怎么样？如果温度超过25度，帮我算一下华氏温度
     * 大模型会依次调用 weather-mcp-service 和 calculator-mcp-service
     */
    @GetMapping("/cross")
    public String crossService(@RequestParam String question) {
        return chatClient.prompt()
                .system("""
                        你是一个综合助手，可以同时使用天气服务和计算服务。
                        当用户询问天气相关问题时，使用天气查询工具。
                        当需要数学计算时，使用计算器工具。
                        可以组合使用多个工具来完成复杂任务。
                        """)
                .user(question)
                .tools(toolCallbackProvider.getToolCallbacks())
                .call()
                .content();
    }
}