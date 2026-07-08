package com.example.integrated;

import com.alibaba.cloud.ai.graph.agent.A2aRemoteAgent;
import com.alibaba.cloud.ai.graph.agent.AgentCardProvider;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 综合演示配置
 * 功能点：同时集成 A2A 远程 Agent 和 MCP 工具，
 * 展示两种远程调用方式的共存与协作
 */
@Configuration
public class IntegratedConfig {

    @Bean
    public A2aRemoteAgent remoteMathAgent(AgentCardProvider agentCardProvider) {
        return A2aRemoteAgent.builder()
                .name("math_agent")
                .agentCardProvider(agentCardProvider)
                .build();
    }

    @Bean
    public A2aRemoteAgent remoteWeatherAgent(AgentCardProvider agentCardProvider) {
        return A2aRemoteAgent.builder()
                .name("weather_agent")
                .agentCardProvider(agentCardProvider)
                .build();
    }
}