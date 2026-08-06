package com.yourproject.config;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Embedding 模型配置
 * 使用百炼（DashScope）的 text-embedding-v3 模型
 * 通过 OpenAI 兼容接口调用
 */
@Configuration
public class EmbeddingConfig {

    private static final Logger log = LoggerFactory.getLogger(EmbeddingConfig.class);

    @Value("${langchain4j.embedding.base-url}")
    private String baseUrl;

    @Value("${langchain4j.embedding.api-key}")
    private String apiKey;

    @Value("${langchain4j.embedding.model-name}")
    private String modelName;

    @Value("${langchain4j.embedding.dimension}")
    private int dimension;

    /**
     * 百炼 Embedding 模型 Bean
     * text-embedding-v3 输出维度 1024
     */
    @Bean
    public EmbeddingModel embeddingModel() {
        log.info("初始化 Embedding 模型: {}，维度: {}，BaseURL: {}", modelName, dimension, baseUrl);
        return OpenAiEmbeddingModel.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .modelName(modelName)
                .build();
    }
}
