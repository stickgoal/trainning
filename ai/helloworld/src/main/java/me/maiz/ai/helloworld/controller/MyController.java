package me.maiz.ai.helloworld.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import lombok.extern.slf4j.Slf4j;
import me.maiz.ai.helloworld.entity.AiMessagesDTO;
import me.maiz.ai.helloworld.param.ChatParam;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
public class MyController {

    @Autowired
    // 声明 ChatClient 实例，用于与 AI 模型进行交互
    private ChatClient chatClient;


    // 定义 GET 请求接口 "/ai"，接收用户输入并返回 AI 生成的内容
    @GetMapping("/ai")
    String generation(String userInput) {
        // 调用 chatClient 发起提示请求，设置用户输入、日志增强器、模型选项，并获取响应内容
        String answer = chatClient.prompt()
                .user(userInput) // 设置用户输入的提示词
                .options(DashScopeChatOptions.builder().temperature(0.7).maxToken(1024).build()) // 设置模型参数：温度 0.7，最大 token 数 1024
                .call() // 执行调用
                .content(); // 获取响应内容
        return answer; // 返回 AI 生成的答案
    }

    /**
     * 处理 SSE (Server-Sent Events) 流式聊天请求，支持多轮对话上下文。
     *
     * @param message 包含历史消息列表的请求体
     * @return Flux<ChatClientResponse> AI 响应的流式数据
     */
    @PostMapping(value = "/ai/sse/{cid}", produces = "text/event-stream;charset=UTF-8")
    public Flux<ChatClientResponse> generationSSE(@RequestBody ChatParam message, @PathVariable("cid") String conversationId) {
        log.info("收到聊天请求: {}，{}", message,conversationId);

        // 构建流式提示请求
        return chatClient.prompt()
                // 设置当前用户的最新输入作为主要提示词
                .user(message.getContent())
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                // 配置模型参数：温度 0.7，最大 token 数 1024
                .options(DashScopeChatOptions.builder().temperature(0.7).maxToken(1024).build())
                // 启用流式响应
                .stream()
                // 返回 ChatClientResponse 类型的流
                .chatClientResponse();
    }
    @GetMapping("/ai/getConversationId")
    public String getConversationId() {
        return UUID.randomUUID().toString();
    }


}
