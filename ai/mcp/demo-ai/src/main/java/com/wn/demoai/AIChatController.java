package com.wn.demoai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/ai")
@Slf4j
public class AIChatController {
    @Autowired
    private ChatClient chatClient;

    @GetMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public String chat(@RequestParam("question") String question, @RequestParam("conversationId") String conversationId) {
        log.info("聊天输入: {},{}", question, conversationId);

        return chatClient.prompt().advisors(a->a.param(ChatMemory.CONVERSATION_ID, conversationId)
                ).user(question).call().content();
    }
    @GetMapping(value = "/getSessionId")
    public String getConversationId() {
        return UUID.randomUUID().toString();
    }

    @GetMapping(value = "/chat/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter aiStreamChat(@RequestParam("question") String question, @RequestParam("conversationId") String conversationId) {
        log.info("接收到 SSE 流式聊天请求，用户输入: {},{}", question,conversationId);
        SseEmitter emitter = new SseEmitter(60_000L);

        // 记录 SSE Emitter 创建成功
        log.debug("SSE Emitter 创建成功，超时时间: 60秒");

        // 异步调用 Spring AI 流式接口
        chatClient
                .prompt()
                .advisors(a->a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .user(question)
                .stream() // 注意：这里返回的是 Flux<ChatResponse>
                .chatResponse()
                .subscribe(
                        response -> {
                            try {
                                String content = response.getResult().getOutput().getText();
                                log.debug("接收到流式响应内容: {}", content);
                                if(Objects.nonNull(content)&& content.length()>0&&content.trim().length()>0) {
                                    log.debug("发送");
                                    emitter.send(SseEmitter.event().data(content));
                                }
                            } catch (Exception e) {
                                log.error("发送 SSE 数据时发生异常: ", e);
                                emitter.completeWithError(e);
                            }
                        },
                        error -> {
                            log.error("流式聊天过程中发生错误: ", error);
                            emitter.completeWithError(error);
                        },
                        () -> {
                            log.info("SSE 流式聊天完成");
                            emitter.complete();
                        }
                );

        return emitter;
    }
}
