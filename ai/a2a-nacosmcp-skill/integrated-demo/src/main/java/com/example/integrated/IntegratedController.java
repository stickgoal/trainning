package com.example.integrated;

import com.alibaba.cloud.ai.graph.agent.A2aRemoteAgent;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * 综合演示控制器
 * 功能点：在同一应用中演示 A2A + MCP + Skill 的联合使用
 *
 * 场景：用户提出一个复杂问题，系统自动路由到不同的能力：
 * - A2A：调用远程 Agent（数学计算、天气查询）
 * - MCP：调用 MCP 工具（计算器、天气服务）
 * - Skill：使用技能系统（文案写作、代码审查）
 */
@RestController
@RequestMapping("/api/integrated")
public class IntegratedController {

    private final A2aRemoteAgent remoteMathAgent;
    private final A2aRemoteAgent remoteWeatherAgent;
    private final ChatClient chatClient;
    private final ToolCallbackProvider toolCallbackProvider;

    public IntegratedController(A2aRemoteAgent remoteMathAgent,
                                 A2aRemoteAgent remoteWeatherAgent,
                                 ChatClient.Builder chatClientBuilder,
                                 ToolCallbackProvider toolCallbackProvider) {
        this.remoteMathAgent = remoteMathAgent;
        this.remoteWeatherAgent = remoteWeatherAgent;
        this.chatClient = chatClientBuilder.build();
        this.toolCallbackProvider = toolCallbackProvider;
    }

    /**
     * A2A + MCP 联合演示
     * 示例：GET /api/integrated/a2a-mcp?question=查询北京天气，并计算25摄氏度转华氏度
     *
     * 功能点：先用 A2A 调用天气 Agent，再用 MCP 工具做计算
     */
    @GetMapping("/a2a-mcp")
    public String a2aMcpDemo(@RequestParam String question) {
        StringBuilder result = new StringBuilder();

        // 步骤1：通过 A2A 调用远程天气 Agent
        result.append("=== A2A 远程 Agent 调用 ===\n");
        Optional<com.alibaba.cloud.ai.graph.OverAllState> weatherResult =
                remoteWeatherAgent.invoke("查询北京的天气");
        weatherResult.ifPresent(state -> {
            Object msgs = state.value("messages");
            if (msgs != null) result.append(msgs.toString()).append("\n");
        });

        // 步骤2：通过 MCP 工具进行计算
        result.append("\n=== MCP 工具调用 ===\n");
        String calcResult = chatClient.prompt()
                .user("计算 25 摄氏度等于多少华氏度（公式：F = C * 9/5 + 32）")
                .tools(toolCallbackProvider.getToolCallbacks())
                .call()
                .content();
        result.append(calcResult);

        return result.toString();
    }

    /**
     * 场景化综合演示
     * 示例：GET /api/integrated/scenario?task=帮我分析一下：如果北京今天天气好，适合户外活动，帮我算一下从公司到天安门广场跑步消耗的卡路里（假设距离10公里，体重70kg）
     *
     * 功能点：复杂场景下自动协调 A2A Agent 和 MCP 工具
     */
    @GetMapping("/scenario")
    public String scenarioDemo(@RequestParam String task) {
        // 使用 ChatClient 作为协调器，同时接入 A2A Agent 和 MCP 工具
        return chatClient.prompt()
                .system("""
                        你是一个综合任务协调器，可以：
                        1. 使用天气查询工具获取天气信息
                        2. 使用计算器工具进行数学计算
                        3. 综合信息给出建议
                        请按步骤完成用户任务，每步说明使用了什么能力。
                        """)
                .user(task)
                .tools(toolCallbackProvider.getToolCallbacks())
                .call()
                .content();
    }

    /**
     * 能力对比演示
     * 示例：GET /api/integrated/compare
     *
     * 功能点：展示 A2A、MCP、Skill 三种能力的使用场景对比
     */
    @GetMapping("/compare")
    public String compareCapabilities() {
        return """
                【Spring AI Alibaba 三种能力对比】

                ┌──────────┬──────────────────┬──────────────────┬──────────────────┐
                │   维度   │   A2A            │   Nacos MCP      │   Skill          │
                ├──────────┼──────────────────┼──────────────────┼──────────────────┤
                │ 定位     │ Agent 级远程调用  │ 工具级远程调用    │ 本地指令+工具封装 │
                │ 注册方式 │ Nacos 服务注册    │ Nacos MCP Registry│ Classpath/文件系统│
                │ 调用方式 │ A2aRemoteAgent   │ ChatClient+tools  │ read_skill 工具   │
                │ 适用场景 │ 多 Agent 协作     │ 工具服务化        │ 领域知识封装      │
                │ 协议     │ A2A (HTTP/REST)  │ MCP (SSE/STDIO)  │ 渐进式披露        │
                │ 粒度     │ 完整 Agent       │ 单个工具函数      │ 技能包(文档+脚本) │
                └──────────┴──────────────────┴──────────────────┴──────────────────┘

                选择建议：
                - 需要完整 Agent 能力（推理+工具+记忆）→ 使用 A2A
                - 需要将工具服务化，跨语言调用 → 使用 Nacos MCP
                - 需要封装领域知识和最佳实践 → 使用 Skill
                - 三者可以组合使用，互不冲突
                """;
    }
}