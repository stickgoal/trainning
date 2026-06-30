package me.maiz.langchain4jdemo.rag.agentic.config;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import me.maiz.langchain4jdemo.rag.agentic.agent.AdmissionAssistantAgent;
import me.maiz.langchain4jdemo.rag.agentic.tool.KnowledgeSearchTool;
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
 * Agentic RAG 配置
 *
 * Agent 自主决策是否需要检索知识库。
 * ContentRetriever 在 KnowledgeSearchTool 内部创建。
 */
@Slf4j
//@Configuration
public class RagAgenticConfig {

    @Bean
    public EmbeddingStore<TextSegment> agenticEmbeddingStore() {
        return new InMemoryEmbeddingStore<>();
    }

    @Bean
    public DocumentSplitter agenticDocumentSplitter() {
        return new DocumentByParagraphSplitter(300, 50);
    }

    @Bean
    public KnowledgeSearchTool knowledgeSearchTool(EmbeddingModel embeddingModel) {
        return new KnowledgeSearchTool(agenticEmbeddingStore(), embeddingModel);
    }

    @Bean
    public AdmissionAssistantAgent admissionAssistantAgent(
            ChatModel chatModel,
            KnowledgeSearchTool knowledgeSearchTool) {
        return AiServices.builder(AdmissionAssistantAgent.class)
                .chatModel(chatModel)
                .tools(knowledgeSearchTool)
                .build();
    }

    @Bean
    public AgenticRagDataLoader agenticRagDataLoader(EmbeddingModel embeddingModel) {
        return new AgenticRagDataLoader(agenticEmbeddingStore(), agenticDocumentSplitter(), embeddingModel);
    }

    @Slf4j
    public static class AgenticRagDataLoader {
        private final EmbeddingStore<TextSegment> store;
        private final DocumentSplitter splitter;
        private final EmbeddingModel embeddingModel;

        public AgenticRagDataLoader(EmbeddingStore<TextSegment> store, DocumentSplitter splitter, EmbeddingModel em) {
            this.store = store; this.splitter = splitter; this.embeddingModel = em;
        }

        @PostConstruct
        public void loadDocuments() {
            log.info("===== [Agentic RAG] 开始加载知识库 =====");
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
                                    documents.add(doc);
                                    log.info("  [Agentic RAG] 加载: {}", file.getFileName());
                                } catch (IOException e) { log.error("读取文档失败", e); }
                            });
                }
                if (!documents.isEmpty()) {
                    EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                            .embeddingStore(store).embeddingModel(embeddingModel).documentSplitter(splitter).build();
                    ingestor.ingest(documents);
                    log.info("===== [Agentic RAG] 知识库加载完成: {} 个文档 =====", documents.size());
                }
            } catch (Exception e) {
                log.error("[Agentic RAG] 知识库加载失败: {}，RAG 功能可能不可用", e.getMessage());
            }
        }
    }
}
