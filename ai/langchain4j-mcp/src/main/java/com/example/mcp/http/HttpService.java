package com.example.mcp.http;

import com.example.mcp.common.FallbackToolProvider;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * HTTP/SSE 传输 MCP 服务。
 */
@Slf4j
@Service
public class HttpService {

    private final HttpAgent httpAgent;
    private final ToolProvider toolProvider;

    @Autowired
    public HttpService(ChatModel chatModel,
                       @Qualifier("httpToolProvider") ToolProvider toolProvider) {
        this.toolProvider = toolProvider;
        log.info("Building HttpAgent with chatModel={}", chatModel);

        this.httpAgent = AiServices.builder(HttpAgent.class)
                .chatModel(chatModel)
                .toolProvider(toolProvider)
                .build();

        log.info("HttpAgent built successfully");
    }

    /**
     * 向 AI 助手提问，通过 HTTP/SSE MCP Server 获取工具能力。
     *
     * @param question 用户问题
     * @return AI 回答；若 MCP Server 未运行则返回清晰提示
     */
    public String ask(String question) {
        log.info("HttpService.ask: question={}", question);

        if (toolProvider instanceof FallbackToolProvider fallback) {
            return fallback.getReason();
        }

        try {
            return httpAgent.ask(question);
        } catch (Exception e) {
            log.error("HttpService failed to get answer", e);
            return "调用 AI 服务时出错：" + e.getMessage();
        }
    }
}
