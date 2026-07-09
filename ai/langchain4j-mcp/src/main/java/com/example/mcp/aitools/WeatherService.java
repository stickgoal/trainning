package com.example.mcp.aitools;

import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * 天气助手服务。
 *
 * 演示 AI Service + MCP Tool Provider 的标准组合模式：
 * 1. 使用 Spring Boot 自动注入的 ChatModel（由 langchain4j-open-ai-spring-boot-starter 提供）
 * 2. 使用 MCP Tool Provider（由 StdioMcpConfig 提供的 stdioToolProvider）
 * 3. 通过 AiServices 组装为 AI Service
 *
 * 这是 LangChain4j 中最常用的 AI Service 模式：
 *   AiServices.builder(接口.class)
 *       .chatModel(model)
 *       .toolProvider(toolProvider)
 *       .build();
 */
@Slf4j
@Service
public class WeatherService {

    private final WeatherAssistant weatherAssistant;

    @Autowired
    public WeatherService(ChatModel chatModel,
                          @Qualifier("stdioToolProvider") McpToolProvider toolProvider) {
        log.info("Building WeatherAssistant with chatModel={}", chatModel);

        this.weatherAssistant = AiServices.builder(WeatherAssistant.class)
                .chatModel(chatModel)
                .toolProvider(toolProvider)
                .build();

        log.info("WeatherAssistant built successfully");
    }

    /**
     * 与天气助手对话。
     *
     * @param question 用户问题
     * @return AI 回答
     */
    public String ask(String question) {
        log.info("WeatherService.ask: question={}", question);
        // TODO: 实现完整的调用逻辑
        return weatherAssistant.chat(question);
    }
}
