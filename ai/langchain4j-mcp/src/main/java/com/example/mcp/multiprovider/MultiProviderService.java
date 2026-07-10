package com.example.mcp.multiprovider;

import com.example.mcp.common.FallbackToolProvider;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * 多 MCP Provider 服务。
 *
 * <p>演示如何将多个 MCP Client 的工具组合使用，
 * 让 LLM 可以调用来自不同 Server 的工具。
 */
@Slf4j
@Service
public class MultiProviderService {

    private final MultiProviderAgent multiProviderAgent;
    private final ToolProvider toolProvider;

    @Autowired
    public MultiProviderService(ChatModel chatModel,
                                @Qualifier("multiToolProvider") ToolProvider toolProvider) {
        this.toolProvider = toolProvider;
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
     * @return AI 回答；若所有 MCP Server 均不可用则返回清晰提示
     */
    public String ask(String question) {
        log.info("MultiProviderService.ask: question={}", question);

        if (toolProvider instanceof FallbackToolProvider fallback) {
            return fallback.getReason();
        }

        try {
            return multiProviderAgent.ask(question);
        } catch (Exception e) {
            log.error("MultiProviderService failed to get answer", e);
            return "调用 AI 服务时出错：" + e.getMessage();
        }
    }
}
