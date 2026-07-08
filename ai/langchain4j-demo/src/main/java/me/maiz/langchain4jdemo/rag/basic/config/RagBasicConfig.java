package me.maiz.langchain4jdemo.rag.basic.config;

import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.document.splitter.DocumentBySentenceSplitter;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import lombok.extern.slf4j.Slf4j;
import me.maiz.langchain4jdemo.rag.basic.assistant.KnowledgeAssistant;
import me.maiz.langchain4jdemo.rag.basic.custom.EmbeddingModelWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 基础 RAG 配置
 *
 * 流程：加载文档 → 切片 → 嵌入 → 存储 → 检索 → 增强生成
 *
 * 关键设计：ContentRetriever 不在 Spring 容器中注册，
 * 而是在 @Bean 方法内部创建后直接传给 AiServices。
 */
@Slf4j
@Configuration
public class RagBasicConfig {

    /**
     * 向量存储（Bean 名称唯一，避免与其他 Config 冲突）
     */
    @Bean
    @Primary
    public EmbeddingStore<TextSegment> basicEmbeddingStore() {
        return new InMemoryEmbeddingStore<>();
    }

    @Bean
    public DocumentSplitter basicDocumentSplitter() {
        return new DocumentBySentenceSplitter(300, 50);
    }

    /**
     * 文档注入器 — 使用 inter-bean 引用避免注入冲突
     */
    @Bean
    public EmbeddingStoreIngestor basicStoreIngestor(EmbeddingModel embeddingModel, EmbeddingStore<TextSegment> embeddingStore) {
        log.info(">>>basicStoreIngestor: embeddingModel={}, embeddingStore={}", embeddingModel, embeddingStore);
        return EmbeddingStoreIngestor.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(new EmbeddingModelWrapper(embeddingModel, 10))
                .documentSplitter(basicDocumentSplitter())
                .build();
    }

    /**
     * AI Service — 内部创建 ContentRetriever，不注册为 Bean
     */
    @Bean
    public KnowledgeAssistant knowledgeAssistant(
            ChatModel chatModel,
            EmbeddingModel embeddingModel,
            EmbeddingStore<TextSegment> embeddingStore) {
        return AiServices.builder(KnowledgeAssistant.class)
                .chatModel(chatModel)
                .contentRetriever(EmbeddingStoreContentRetriever.builder()
                        .embeddingStore(embeddingStore)
                        .embeddingModel(embeddingModel)
                        .maxResults(5)
                        .minScore(0.5)
                        .build())
                .build();
    }

}
