package com.example.mcp.multiprovider;

import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * 多 MCP Provider 服务。
 *
 * 演示如何将多个 MCP Client 的工具组合使用，
 * 让 LLM 可以调用来自不同 Server 的工具。
 */
@Slf4j
@Service
public class MultiProviderService {

    private final MultiProviderAgent multiProviderAgent;

    @Autowired
    public MultiProviderService(ChatModel chatModel,
                                @Qualifier("multiToolProvider") McpToolProvider toolProvider) {
        log.info("Building MultiProviderAgent with chatModel={}", chatModel);

        this.multiProviderAgent = AiServices.builder(MultiProviderAgent.class)
                .chatModel(chatModel)
                .toolProvider(toolProvider)
                .build();

        log.info("MultiProviderAgent built successfully");
    }

    /**
     * 向 AI 助手提问，使用多个 MCP Server 的工具。
     *
     * @param question 用户问题
     * @return AI 回答
     */
    public String ask(String question) {
        log.info("MultiProviderService.ask: question={}", question);
        // TODO: 实现完整的调用逻辑
        return multiProviderAgent.ask(question);
    }
}
