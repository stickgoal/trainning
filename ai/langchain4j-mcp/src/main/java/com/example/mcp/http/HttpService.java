package com.example.mcp.http;

import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
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

    @Autowired
    public HttpService(ChatModel chatModel,
                       @Qualifier("httpToolProvider") McpToolProvider toolProvider) {
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
     * @return AI 回答
     */
    public String ask(String question) {
        log.info("HttpService.ask: question={}", question);
        // TODO: 实现完整的调用逻辑
        return httpAgent.ask(question);
    }
}
