package me.maiz.langchain4jdemo.rag.basic.controller;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import me.maiz.langchain4jdemo.rag.basic.assistant.KnowledgeAssistant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * 基础 RAG — 蜗牛学苑知识库问答
 *
 * 通过 RAG 从知识库检索相关文档内容，再由 LLM 生成准确回答。
 */
@Slf4j
@RestController
@RequestMapping("/rag/basic")
public class RagBasicController {

    @Autowired
    private KnowledgeAssistant knowledgeAssistant;

    @Autowired
    private  EmbeddingStoreIngestor ingestor;

    @RequestMapping("/load")
    public void loadDocuments() {
        log.info("===== [基础RAG] 开始加载知识库 =====");
        try {
            List<Document> documents = loadDocs();
            if (!documents.isEmpty()) {
                ingestor.ingest(documents);
                log.info("===== [基础RAG] 知识库加载完成: {} 个文档 =====", documents.size());
            }
        } catch (Exception e) {
            log.error("[基础RAG] 知识库加载失败: {}，RAG 功能可能不可用", e.getMessage());
        }
    }

    private List<Document> loadDocs() {
        try {
            return loadDocsInternal();
        } catch (Exception e) {
            log.warn("读取文档失败: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    private List<Document> loadDocsInternal() throws IOException, URISyntaxException {
        List<Document> documents = new ArrayList<>();
        URL dirUrl = Thread.currentThread().getContextClassLoader().getResource("rag-docs");
        if (dirUrl == null) return documents;
        Path dirPath = Paths.get(dirUrl.toURI());
        if (!Files.isDirectory(dirPath)) return documents;
        try (Stream<Path> files = Files.list(dirPath)) {
            files.filter(p -> p.toString().endsWith(".txt"))
                    .forEach(file -> {
                        try {
                            String content = Files.readString(file);
                            Document doc = Document.from(content);
                            doc.metadata().put("source", file.getFileName().toString());
                            documents.add(doc);
                            log.info("  [基础RAG] 加载: {}", file.getFileName());
                        } catch (IOException e) {
                            log.error("读取文档失败", e);
                        }
                    });
        }
        return documents;
    }

    /**
     * 基于知识库回答问题
     *
     * @param question 用户问题，如"Java课程多少钱"
     * @return 基于知识库检索的 AI 回答
     */
    @GetMapping("/ask")
    public String ask(@RequestParam String question) {
        log.info("RAG 基础问答 - 问题: {}", question);
        long start = System.currentTimeMillis();
        try {
            String answer = knowledgeAssistant.answer(question);
            long elapsed = System.currentTimeMillis() - start;
            log.info("回答完成，耗时: {}ms", elapsed);
            return answer;
        } catch (Exception e) {
            log.error("RAG 问答失败", e);
            return "抱歉，回答时出现错误: " + e.getMessage();
        }
    }
}
