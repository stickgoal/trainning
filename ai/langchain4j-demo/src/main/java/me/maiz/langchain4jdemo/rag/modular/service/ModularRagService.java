package me.maiz.langchain4jdemo.rag.modular.service;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.aggregator.ContentAggregator;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.store.embedding.EmbeddingStore;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 模块化 RAG 服务
 *
 * 显式编排 RAG 各模块：检索 → 聚合 → 生成。
 * ContentRetriever 在 @PostConstruct 中初始化（不注册为 Spring Bean）。
 */
@Slf4j
@Service
public class ModularRagService {

    @Autowired
    private ChatModel chatModel;

    @Autowired
    @Qualifier("modularEmbeddingStore")
    private EmbeddingStore<TextSegment> modularEmbeddingStore;

    @Autowired
    private EmbeddingModel embeddingModel;

    @Autowired
    private ContentAggregator modularContentAggregator;

    private ContentRetriever contentRetriever;

    @PostConstruct
    public void init() {
        // 内部创建 ContentRetriever，不暴露为 Spring Bean
        this.contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(modularEmbeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(5)
                .minScore(0.4)
                .build();
        log.info("模块化 RAG 服务初始化完成");
    }

    /**
     * 执行模块化 RAG 问答
     */
    public RagResult answer(String question) {
        log.info("===== 模块化 RAG 开始 =====");
        log.info("① 原始问题: {}", question);

        Query query = Query.from(question);
        log.info("② Query 构建完成");

        // ===== 步骤 2：内容检索 =====
        long start = System.currentTimeMillis();
        List<Content> retrieved = contentRetriever.retrieve(query);
        long retrieveTime = System.currentTimeMillis() - start;
        log.info("③ 检索到 {} 条内容，耗时 {}ms", retrieved.size(), retrieveTime);

        List<String> retrievedSnippets = retrieved.stream()
                .map(c -> {
                    String text = c.textSegment() != null ? c.textSegment().text() : "";
                    return text.length() > 80 ? text.substring(0, 80) + "..." : text;
                })
                .toList();
        log.info("   检索摘要: {}", retrievedSnippets);

        // ===== 步骤 3：内容聚合 =====
        Map<Query, Collection<List<Content>>> aggregatorInput = new HashMap<>();
        aggregatorInput.put(query, List.of(retrieved));

        start = System.currentTimeMillis();
        List<Content> aggregated = modularContentAggregator.aggregate(aggregatorInput);
        long aggregateTime = System.currentTimeMillis() - start;
        log.info("④ 聚合后保留 {} 条，耗时 {}ms", aggregated.size(), aggregateTime);

        // ===== 步骤 4：组装 Prompt =====
        StringBuilder context = new StringBuilder();
        for (int i = 0; i < aggregated.size(); i++) {
            String text = aggregated.get(i).textSegment() != null
                    ? aggregated.get(i).textSegment().text()
                    : "";
            context.append("【参考片段 ").append(i + 1).append("】\n")
                    .append(text).append("\n\n");
        }

        String prompt = """
                你是一位专业的蜗牛学苑客服顾问。请基于以下知识库内容回答用户问题。
                如果知识库中没有相关信息，请如实告知，不要编造。

                知识库内容：
                %s

                用户问题：%s

                请基于上述知识库内容给出准确、详细的回答。
                """.formatted(context.toString(), question);

        // ===== 步骤 5：LLM 生成 =====
        start = System.currentTimeMillis();
        String answer = chatModel.chat(prompt);
        long generateTime = System.currentTimeMillis() - start;

        log.info("===== 模块化 RAG 结束，总耗时 {}ms =====",
                retrieveTime + aggregateTime + generateTime);

        return RagResult.builder()
                .question(question)
                .answer(answer)
                .retrievedCount(retrieved.size())
                .aggregatedCount(aggregated.size())
                .retrieveTimeMs(retrieveTime)
                .aggregateTimeMs(aggregateTime)
                .generateTimeMs(generateTime)
                .totalTimeMs(retrieveTime + aggregateTime + generateTime)
                .retrievedSnippets(retrievedSnippets)
                .build();
    }
}
