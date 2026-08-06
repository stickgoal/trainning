package com.yourproject.config;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RAG 核心配置
 * 组装 ChatModel + EmbeddingModel + RetrievalAugmentor
 */
@Configuration
public class RAGConfig {

    private static final Logger log = LoggerFactory.getLogger(RAGConfig.class);

    @Value("${langchain4j.chat.base-url}")
    private String chatBaseUrl;

    @Value("${langchain4j.chat.api-key}")
    private String chatApiKey;

    @Value("${langchain4j.chat.model-name}")
    private String chatModelName;

    /**
     * 百炼 Chat 模型（qwen-plus）
     * 通过 OpenAI 兼容接口调用
     */
    @Bean
    public ChatModel chatModel() {
        log.info("初始化 ChatModel: {}，BaseURL: {}", chatModelName, chatBaseUrl);
        return OpenAiChatModel.builder()
                .baseUrl(chatBaseUrl)
                .apiKey(chatApiKey)
                .modelName(chatModelName)
                .temperature(0.3) // RAG 场景使用低温度，减少创造性
                .timeout(java.time.Duration.ofSeconds(60))
                .build();
    }
}
