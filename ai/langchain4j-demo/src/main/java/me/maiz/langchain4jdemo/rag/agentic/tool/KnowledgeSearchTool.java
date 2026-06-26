package me.maiz.langchain4jdemo.rag.agentic.tool;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.store.embedding.EmbeddingStore;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 知识检索工具
 *
 * Agent 在遇到不确定的问题时自主决定调用此工具检索知识库。
 *
 * 对比非 Agentic RAG：
 * - 基础/进阶/模块化 RAG：每次对话都强制检索
 * - Agentic RAG：Agent 自主判断是否需要检索
 */
@Slf4j
public class KnowledgeSearchTool {

    private final EmbeddingStore<TextSegment> embeddingStore;
    private final EmbeddingModel embeddingModel;
    private ContentRetriever contentRetriever;

    public KnowledgeSearchTool(EmbeddingStore<TextSegment> embeddingStore, EmbeddingModel embeddingModel) {
        this.embeddingStore = embeddingStore;
        this.embeddingModel = embeddingModel;
    }

    @PostConstruct
    public void init() {
        // 内部创建 ContentRetriever，不暴露为 Spring Bean
        this.contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(3)
                .minScore(0.5)
                .build();
        log.info("KnowledgeSearchTool 初始化完成");
    }

    /**
     * 搜索蜗牛学苑知识库
     *
     * @param query 搜索关键词
     * @return 相关知识库内容
     */
    @Tool("搜索蜗牛学苑知识库，获取课程、校区、报名、学费等相关信息")
    public String searchKnowledge(String query) {
        log.info("🔍 Agent 调用了知识检索工具，查询: {}", query);

        List<Content> results = contentRetriever.retrieve(Query.from(query));

        if (results.isEmpty()) {
            log.info("未找到相关信息");
            return "知识库中未找到与「" + query + "」相关的信息。";
        }

        String result = results.stream()
                .map(content -> content.textSegment() != null ? content.textSegment().text() : "")
                .collect(Collectors.joining("\n---\n"));

        log.info("检索到 {} 条结果", results.size());
        return result;
    }
}
