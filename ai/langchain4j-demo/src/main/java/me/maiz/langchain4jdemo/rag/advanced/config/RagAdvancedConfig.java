package me.maiz.langchain4jdemo.rag.advanced.config;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;


import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import me.maiz.langchain4jdemo.rag.advanced.assistant.AdvancedKnowledgeAssistant;
import me.maiz.langchain4jdemo.rag.advanced.query.QueryRewriteTransformer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * 进阶 RAG 配置
 *
 * 演示：查询重写 + 多路检索
 * 流程：用户查询 → 查询重写(LLM) → 多路检索 → 增强生成
 *
 * 使用 inter-bean 引用避免 Spring 注入冲突。
 */
@Slf4j
// @Configuration
public class RagAdvancedConfig {

    // ========== 向量存储 ==========

    // @Bean
    public EmbeddingStore<TextSegment> advancedCourseEmbeddingStore() {
        return new InMemoryEmbeddingStore<>();
    }

    // @Bean
    public EmbeddingStore<TextSegment> advancedFaqEmbeddingStore() {
        return new InMemoryEmbeddingStore<>();
    }

    // @Bean
    public DocumentSplitter advancedDocumentSplitter() {
        return new DocumentByParagraphSplitter(300, 50);
    }

    // @Bean
    public QueryRewriteTransformer advancedQueryRewriteTransformer(ChatModel chatModel) {
        return new QueryRewriteTransformer(chatModel);
    }

    // ========== 文档注入 ==========

    // @Bean
    public AdvancedRagDataLoader advancedRagDataLoader(EmbeddingModel embeddingModel) {
        return new AdvancedRagDataLoader(
                advancedCourseEmbeddingStore(),
                advancedFaqEmbeddingStore(),
                advancedDocumentSplitter(),
                embeddingModel);
    }

    // ========== Retrieval Augmentor ==========

    // @Bean
    public dev.langchain4j.rag.RetrievalAugmentor advancedRetrievalAugmentor(
            EmbeddingModel embeddingModel,
            QueryRewriteTransformer queryRewriteTransformer) {

        ContentRetriever courseRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(advancedCourseEmbeddingStore())
                .embeddingModel(embeddingModel)
                .maxResults(2)
                .minScore(0.5)
                .build();

        ContentRetriever faqRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(advancedFaqEmbeddingStore())
                .embeddingModel(embeddingModel)
                .maxResults(2)
                .minScore(0.5)
                .build();

        ContentRetriever compositeRetriever = query -> {
            List<dev.langchain4j.rag.content.Content> results = new ArrayList<>();
            results.addAll(courseRetriever.retrieve(query));
            results.addAll(faqRetriever.retrieve(query));
            log.info("进阶RAG 复合检索返回 {} 条结果", results.size());
            return results;
        };

        return DefaultRetrievalAugmentor.builder()
                .queryTransformer(queryRewriteTransformer)
                .contentRetriever(compositeRetriever)
                .build();
    }

    // @Bean
    public AdvancedKnowledgeAssistant advancedKnowledgeAssistant(
            ChatModel chatModel,
            dev.langchain4j.rag.RetrievalAugmentor retrievalAugmentor) {
        return AiServices.builder(AdvancedKnowledgeAssistant.class)
                .chatModel(chatModel)
                .retrievalAugmentor(retrievalAugmentor)
                .build();
    }

    @Slf4j
    public static class AdvancedRagDataLoader {
        private final EmbeddingStore<TextSegment> courseStore;
        private final EmbeddingStore<TextSegment> faqStore;
        private final DocumentSplitter splitter;
        private final EmbeddingModel embeddingModel;

        public AdvancedRagDataLoader(EmbeddingStore<TextSegment> cs, EmbeddingStore<TextSegment> fs,
                                     DocumentSplitter sp, EmbeddingModel em) {
            this.courseStore = cs; this.faqStore = fs; this.splitter = sp; this.embeddingModel = em;
        }

        // @PostConstruct
        public void loadDocuments() {
            log.info("===== [进阶RAG] 开始加载知识库 =====");
            try {
                List<Document> courseDocs = loadDocsByPrefix("courses");
                if (!courseDocs.isEmpty()) {
                    EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                            .embeddingStore(courseStore).embeddingModel(embeddingModel).documentSplitter(splitter).build();
                    ingestor.ingest(courseDocs);
                    log.info("[进阶RAG] 课程库加载完成: {} 个文档", courseDocs.size());
                }
                List<Document> faqDocs = loadDocsByPrefix("faq");
                if (!faqDocs.isEmpty()) {
                    EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                            .embeddingStore(faqStore).embeddingModel(embeddingModel).documentSplitter(splitter).build();
                    ingestor.ingest(faqDocs);
                    log.info("[进阶RAG] FAQ库加载完成: {} 个文档", faqDocs.size());
                }
            } catch (Exception e) {
                log.error("[进阶RAG] 知识库加载失败: {}，RAG 功能可能不可用", e.getMessage());
            }
            log.info("===== [进阶RAG] 知识库加载完成 =====");
        }

        private List<Document> loadDocsByPrefix(String prefix) {
            List<Document> documents = new ArrayList<>();
            try {
                URL dirUrl = Thread.currentThread().getContextClassLoader().getResource("rag-docs");
                if (dirUrl == null) return documents;
                Path dirPath = Paths.get(dirUrl.toURI());
                if (!Files.isDirectory(dirPath)) return documents;
                try (Stream<Path> files = Files.list(dirPath)) {
                    files.filter(p -> p.getFileName().toString().startsWith(prefix) && p.toString().endsWith(".txt"))
                            .forEach(file -> {
                                try {
                                    String content = Files.readString(file);
                                    Document doc = Document.from(content);
                                    doc.metadata().put("source", file.getFileName().toString());
                                    doc.metadata().put("type", prefix);
                                    documents.add(doc);
                                    log.info("  [进阶RAG] 加载: {}", file.getFileName());
                                } catch (IOException e) { log.error("读取文档失败", e); }
                            });
                }
            } catch (Exception e) {
                log.warn("读取文档目录失败: {}", e.getMessage());
            }
            return documents;
        }
    }
}
