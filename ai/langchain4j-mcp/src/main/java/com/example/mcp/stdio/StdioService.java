package com.example.mcp.stdio;

import com.example.mcp.common.FallbackToolProvider;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * stdio 传输 MCP 服务。
 *
 * <p>使用 AiServices 将 ChatModel + ToolProvider 组装为 AI Service，
 * 用户提问时 LLM 会自动决定是否调用 MCP Server 提供的工具。
 */
@Slf4j
@Service
public class StdioService {

    private final StdioAgent stdioAgent;
    private final ToolProvider toolProvider;

    @Autowired
    public StdioService(ChatModel chatModel,
                        @Qualifier("stdioToolProvider") ToolProvider toolProvider) {
        this.toolProvider = toolProvider;
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
     * @return AI 回答；若 MCP 工具不可用则返回清晰提示
     */
    public String ask(String question) {
        log.info("StdioService.ask: question={}", question);

        // MCP Server 不可用时，直接返回原因，避免无意义的 LLM 调用
        if (toolProvider instanceof FallbackToolProvider fallback) {
            return fallback.getReason();
        }

        try {
            return stdioAgent.ask(question);
        } catch (Exception e) {
            log.error("StdioService failed to get answer", e);
            return "调用 AI 服务时出错：" + e.getMessage();
        }
    }
}
