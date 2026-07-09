package com.example.mcp.stdio;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import dev.langchain4j.mcp.McpToolProvider;

/**
 * stdio 传输 MCP 服务。
 *
 * 使用 AiServices 将 ChatModel + McpToolProvider 组装为 AI Service，
 * 用户提问时 LLM 会自动决定是否调用 MCP Server 提供的工具。
 */
@Slf4j
@Service
public class StdioService {

    private final StdioAgent stdioAgent;

    @Autowired
    public StdioService(ChatModel chatModel,
                        @Qualifier("stdioToolProvider") McpToolProvider toolProvider) {
        log.info("Building StdioAgent with chatModel={}", chatModel);

        this.stdioAgent = AiServices.builder(StdioAgent.class)
                .chatModel(chatModel)
                .toolProvider(toolProvider)
                .build();

        log.info("StdioAgent built successfully");
    }

    /**
     * 向 AI 助手提问，自动调用 MCP 工具并返回回答。
     *
     * @param question 用户问题
     * @return AI 回答
     */
    public String ask(String question) {
        log.info("StdioService.ask: question={}", question);
        // TODO: 实现完整的调用逻辑，包括异常处理和结果格式化
        return stdioAgent.ask(question);
    }
}
