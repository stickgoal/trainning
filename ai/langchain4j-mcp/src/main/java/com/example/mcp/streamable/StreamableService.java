package com.example.mcp.streamable;

import com.example.mcp.common.FallbackToolProvider;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolProvider;
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
    private final ToolProvider toolProvider;

    @Autowired
    public StreamableService(ChatModel chatModel,
                             @Qualifier("streamableToolProvider") ToolProvider toolProvider) {
        this.toolProvider = toolProvider;
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
     * @return AI 回答；若 MCP Server 未运行则返回清晰提示
     */
    public String ask(String question) {
        log.info("StreamableService.ask: question={}", question);

        if (toolProvider instanceof FallbackToolProvider fallback) {
            return fallback.getReason();
        }

        try {
            return streamableAgent.ask(question);
        } catch (Exception e) {
            log.error("StreamableService failed to get answer", e);
            return "调用 AI 服务时出错：" + e.getMessage();
        }
    }
}
