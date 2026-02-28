package com.wn.demoai.config;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AIChatConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder, ToolCallbackProvider toolCallbackProvider) {
        return builder
                .defaultSystem("你是一个资深的导购人员，你很专业，按照用户的需要提供合适的建议，并且根据内部的数据给出合理的回答") //系统输入
                .defaultAdvisors(new SimpleLoggerAdvisor(),
                        MessageChatMemoryAdvisor.builder(MessageWindowChatMemory.builder().maxMessages(10).build()).build() //增加记忆功能
                )
                .defaultToolCallbacks(toolCallbackProvider)
                .defaultOptions(DashScopeChatOptions.builder()
                        .withTemperature(0.2) // 模型温度,0-1之间,值越大，模型越随机,越活跃，值越小，越保守
                        .build())
                .build();
    }


}
