package com.example.mcp.streamable;

import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Streamable HTTP 传输 MCP 服务。
 */
@Slf4j
@Service
public class StreamableService {

    private final StreamableAgent streamableAgent;

    @Autowired
    public StreamableService(ChatModel chatModel,
                             @Qualifier("streamableToolProvider") McpToolProvider toolProvider) {
        log.info("Building StreamableAgent with chatModel={}", chatModel);

        this.streamableAgent = AiServices.builder(StreamableAgent.class)
                .chatModel(chatModel)
                .toolProvider(toolProvider)
                .build();

        log.info("StreamableAgent built successfully");
    }

    /**
     * 向 AI 助手提问，通过 Streamable HTTP MCP Server 获取工具能力。
     *
     * @param question 用户问题
     * @return AI 回答
     */
    public String ask(String question) {
        log.info("StreamableService.ask: question={}", question);
        // TODO: 实现完整的调用逻辑
        return streamableAgent.ask(question);
    }
}
