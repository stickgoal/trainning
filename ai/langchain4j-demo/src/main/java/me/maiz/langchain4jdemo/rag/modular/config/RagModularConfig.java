package me.maiz.langchain4jdemo.rag.modular.config;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.aggregator.ContentAggregator;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import me.maiz.langchain4jdemo.rag.modular.service.ModularContentAggregator;
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
 * 模块化 RAG 配置
 *
 * RAG 流程拆分为独立模块：ContentRetriever → ContentAggregator → LLM
 * ContentRetriever 不在 Spring 中注册为 Bean。
 */
@Slf4j
//@Configuration
public class RagModularConfig {

    @Bean
    public EmbeddingStore<TextSegment> modularEmbeddingStore() {
        return new InMemoryEmbeddingStore<>();
    }

    @Bean
    public DocumentSplitter modularDocumentSplitter() {
        return new DocumentByParagraphSplitter(300, 50);
    }

    @Bean
    public ContentAggregator modularContentAggregator() {
        return new ModularContentAggregator();
    }

    @Bean
    public ModularRagDataLoader modularRagDataLoader(EmbeddingModel embeddingModel) {
        return new ModularRagDataLoader(modularEmbeddingStore(), modularDocumentSplitter(), embeddingModel);
    }

    @Slf4j
    public static class ModularRagDataLoader {
        private final EmbeddingStore<TextSegment> store;
        private final DocumentSplitter splitter;
        private final EmbeddingModel embeddingModel;

        public ModularRagDataLoader(EmbeddingStore<TextSegment> store, DocumentSplitter splitter, EmbeddingModel em) {
            this.store = store; this.splitter = splitter; this.embeddingModel = em;
        }

        @PostConstruct
        public void loadDocuments() {
            log.info("===== [模块化RAG] 开始加载知识库 =====");
            try {
                List<Document> documents = new ArrayList<>();
                URL dirUrl = Thread.currentThread().getContextClassLoader().getResource("rag-docs");
                if (dirUrl == null) return;
                Path dirPath = Paths.get(dirUrl.toURI());
                if (!Files.isDirectory(dirPath)) return;
                try (Stream<Path> files = Files.list(dirPath)) {
                    files.filter(p -> p.toString().endsWith(".txt"))
                            .forEach(file -> {
                                try {
                                    String content = Files.readString(file);
                                    Document doc = Document.from(content);
                                    doc.metadata().put("source", file.getFileName().toString());
                                    doc.metadata().put("docType", classifyDocType(file.getFileName().toString()));
                                    documents.add(doc);
                                    log.info("  [模块化RAG] 加载: {}", file.getFileName());
                                } catch (IOException e) { log.error("读取文档失败", e); }
                            });
                }
                if (!documents.isEmpty()) {
                    EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                            .embeddingStore(store).embeddingModel(embeddingModel).documentSplitter(splitter).build();
                    ingestor.ingest(documents);
                    log.info("===== [模块化RAG] 知识库加载完成: {} 个文档 =====", documents.size());
                }
            } catch (Exception e) {
                log.error("[模块化RAG] 知识库加载失败: RAG 功能可能不可用",e);
            }
        }

        private String classifyDocType(String fileName) {
            if (fileName.startsWith("courses")) return "课程信息";
            if (fileName.startsWith("faq")) return "常见问题";
            if (fileName.startsWith("campus")) return "校区信息";
            return "其他";
        }
    }
}
