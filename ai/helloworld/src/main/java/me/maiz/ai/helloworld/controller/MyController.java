package me.maiz.ai.helloworld.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import lombok.extern.slf4j.Slf4j;
import me.maiz.ai.helloworld.entity.AiMessagesDTO;
import me.maiz.ai.helloworld.param.ChatParam;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@Slf4j
public class MyController {

    // 声明 ChatClient 实例，用于与 AI 模型进行交互
    private final ChatClient chatClient;

    // 构造函数，通过注入 ChatClient.Builder 来构建 ChatClient 实例
    public MyController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    // 定义 GET 请求接口 "/ai"，接收用户输入并返回 AI 生成的内容
    @GetMapping("/ai")
    String generation(String userInput) {
        // 调用 chatClient 发起提示请求，设置用户输入、日志增强器、模型选项，并获取响应内容
        String answer = chatClient.prompt()
                .user(userInput) // 设置用户输入的提示词
                .advisors(new SimpleLoggerAdvisor()) // 添加简单的日志记录增强器
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
    @PostMapping(value = "/ai/sse", produces = "text/event-stream;charset=UTF-8")
    public Flux<ChatClientResponse> generationSSE(@RequestBody AiMessagesDTO message) {
        log.info("收到聊天请求: {}", message);
        
        // 获取历史消息列表
        List<ChatParam> messages = message.getMessages();
        
        // 构建流式提示请求
        return chatClient.prompt()
                // 设置当前用户的最新输入作为主要提示词
                .user(messages.get(messages.size() - 1).getContent())
                // 添加历史消息上下文以支持多轮对话
                .messages(toSpringMessages(message))
                // 配置模型参数：温度 0.7，最大 token 数 1024
                .options(DashScopeChatOptions.builder().temperature(0.7).maxToken(1024).build())
                // 启用流式响应
                .stream()
                // 返回 ChatClientResponse 类型的流
                .chatClientResponse();
    }

    /**
     * 将自定义的消息 DTO 列表转换为 Spring AI 的 Message 对象列表。
     *
     * @param request 包含原始消息列表的请求对象
     * @return List<Message> 转换后的 Spring AI 消息列表
     */
    private List<Message> toSpringMessages(AiMessagesDTO request) {
        List<Message> springMessages = request.getMessages().stream()
                .map(m -> {
                    // 根据角色类型创建对应的消息对象
                    if ("USER".equalsIgnoreCase(m.getRole())) {
                        return new UserMessage(m.getContent());
                    }
                    if ("ASSISTANT".equalsIgnoreCase(m.getRole())) {
                        return (Message)new AssistantMessage(m.getContent());
                    }
                    // 如果角色未知，抛出异常
                    throw new IllegalArgumentException("未知的消息角色: " + m.getRole());
                })
                .toList();
        return springMessages;
    }


}
