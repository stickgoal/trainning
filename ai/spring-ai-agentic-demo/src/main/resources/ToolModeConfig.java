package com.example.agentic.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * 工具提供者配置：根据 app.tool-mode 决定使用 Function Calling 还是 MCP。
 * <p>
 * function-calling 模式：使用 AfterSalesToolConfig 中注册的 FunctionToolCallback Bean
 * mcp 模式：使用 Spring AI MCP Client 自动发现的 MCP ToolCallback
 * <p>
 * Service 层只需注入 ToolCallback[]，无需关心来源。
 */
@Slf4j
@Configuration
public class ToolModeConfig {

    @Value("${app.tool-mode:function-calling}")
    private String toolMode;

    /**
     * Function Calling 模式下的工具数组。
     * 合并售后查询工具 + Supervisor 子 Agent 工具。
     */
    @Bean
    @ConditionalOnProperty(name = "app.tool-mode", havingValue = "function-calling", matchIfMissing = true)
    public ToolCallback[] aftersalesToolCallbacks(
            ToolCallback queryOrderTool,
            ToolCallback queryProductTool,
            ToolCallback queryUserTool,
            ToolCallback queryUserByOrderTool,
            ToolCallback emotionAnalysisTool,
            ToolCallback factInvestigationTool,
            ToolCallback solutionDesignTool,
            ToolCallback notificationDraftTool) {
        log.info("Tool mode: FUNCTION-CALLING — using local FunctionToolCallback beans");
        return new ToolCallback[]{
                queryOrderTool, queryProductTool, queryUserTool, queryUserByOrderTool,
                emotionAnalysisTool, factInvestigationTool, solutionDesignTool, notificationDraftTool
        };
    }

    /**
     * MCP 模式下的工具数组。
     * 从 Spring AI MCP Client 获取售后查询工具，再合并本地子 Agent 工具。
     */
    @Bean
    @ConditionalOnProperty(name = "app.tool-mode", havingValue = "mcp")
    public ToolCallback[] mcpToolCallbacks(
            ToolCallbackProvider toolCallbackProvider,
            ToolCallback emotionAnalysisTool,
            ToolCallback factInvestigationTool,
            ToolCallback solutionDesignTool,
            ToolCallback notificationDraftTool) {
        log.info("Tool mode: MCP — using tools from MCP Server via stdio + local sub-agent tools");
        List<ToolCallback> allTools = new ArrayList<>();
        // MCP 提供的售后查询工具
        for (ToolCallback cb : toolCallbackProvider.getToolCallbacks()) {
            allTools.add(cb);
        }
        // 本地子 Agent 工具（不走 MCP）
        allTools.add(emotionAnalysisTool);
        allTools.add(factInvestigationTool);
        allTools.add(solutionDesignTool);
        allTools.add(notificationDraftTool);
        return allTools.toArray(new ToolCallback[0]);
    }
}
