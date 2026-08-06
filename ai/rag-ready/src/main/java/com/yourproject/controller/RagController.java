package com.yourproject.controller;

import com.yourproject.service.RagService;
import com.yourproject.service.RagService.RagAnswer;
import com.yourproject.service.RagService.RetrievalInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * RAG REST API
 * 提供文档入库、问答、知识库管理等接口
 */
@RestController
@RequestMapping("/api/rag")
public class RagController {

    private static final Logger log = LoggerFactory.getLogger(RagController.class);

    private final RagService ragService;

    public RagController(RagService ragService) {
        this.ragService = ragService;
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> result = new HashMap<>();
        result.put("status", "UP");
        result.put("service", "rag-ready");
        return ResponseEntity.ok(result);
    }

    /**
     * 文档入库
     */
    @PostMapping("/ingest")
    public ResponseEntity<Map<String, Object>> ingest(
            @RequestParam String filePath,
            @RequestParam(defaultValue = "general") String kbName) {
        log.info("API 入库请求: file={}, kb={}", filePath, kbName);
        int count = ragService.ingestDocument(filePath, kbName);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("indexedChunks", count);
        result.put("knowledgeBase", kbName);
        return ResponseEntity.ok(result);
    }

    /**
     * 目录批量入库
     */
    @PostMapping("/ingest-directory")
    public ResponseEntity<Map<String, Object>> ingestDirectory(
            @RequestParam String dirPath,
            @RequestParam(defaultValue = "general") String kbName) {
        log.info("API 批量入库请求: dir={}, kb={}", dirPath, kbName);
        int count = ragService.ingestDirectory(dirPath, kbName);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("indexedChunks", count);
        result.put("knowledgeBase", kbName);
        return ResponseEntity.ok(result);
    }

    /**
     * RAG 问答
     */
    @PostMapping("/ask")
    public ResponseEntity<RagAnswer> ask(
            @RequestParam String query,
            @RequestParam(required = false) String kbName) {
        log.info("API 问答请求: query={}, kb={}", query, kbName);
        RagAnswer answer = ragService.ask(query, kbName);
        return ResponseEntity.ok(answer);
    }

    /**
     * 初始化所有知识库索引
     */
    @PostMapping("/init-indexes")
    public ResponseEntity<Map<String, Object>> initIndexes() {
        ragService.initAllIndexes();
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "所有知识库索引已初始化");
        return ResponseEntity.ok(result);
    }

    /**
     * 列出所有知识库
     */
    @GetMapping("/knowledge-bases")
    public ResponseEntity<Set<String>> listKnowledgeBases() {
        return ResponseEntity.ok(ragService.getKbRouter().getKnowledgeBaseNames());
    }
}
