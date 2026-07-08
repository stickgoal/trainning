package com.example.a2a.client;

import com.alibaba.cloud.ai.graph.agent.A2aRemoteAgent;
import com.alibaba.cloud.ai.graph.agent.AgentCardProvider;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * A2A 客户端配置
 * 功能点：通过 AgentCardProvider 从 Nacos 发现远程 Agent，
 * 创建 A2aRemoteAgent 实现透明的远程 Agent 调用
 */
@Configuration
public class A2aClientConfig {

    /**
     * 远程数学智能体代理
     * 功能点：通过名称 "math_agent" 从 Nacos 自动发现服务，
     * 调用方式与本地 Agent 完全一致
     */
    @Bean
    public A2aRemoteAgent remoteMathAgent(AgentCardProvider agentCardProvider) {
        return A2aRemoteAgent.builder()
                .name("math_agent")
                .agentCardProvider(agentCardProvider)
                .build();
    }

    /**
     * 远程天气智能体代理
     * 功能点：通过名称 "weather_agent" 从 Nacos 自动发现服务
     */
    @Bean
    public A2aRemoteAgent remoteWeatherAgent(AgentCardProvider agentCardProvider) {
        return A2aRemoteAgent.builder()
                .name("weather_agent")
                .agentCardProvider(agentCardProvider)
                .build();
    }
}