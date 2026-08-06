package com.yourproject.service;

import com.yourproject.config.RAGProperties;
import com.yourproject.document.DocumentParser;
import com.yourproject.document.DocumentSplitterConfig;
import com.yourproject.retrieval.KnowledgeBaseRouter;
import com.yourproject.retrieval.RedisHybridRetriever;
import com.yourproject.retrieval.RrfFusion;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.data.embedding.Embedding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * RAG 问答服务
 * 对外提供文档入库 + 混合检索 + 增强生成 的完整 RAG 链路
 */
@Service
public class RagService {

    private static final Logger log = LoggerFactory.getLogger(RagService.class);

    private static final String SYSTEM_PROMPT = """
            你是一个专业的知识库问答助手。请严格遵循以下规则：
            
            1. 仅基于【检索到的上下文】回答问题，禁止编造或使用外部知识。
            2. 如果检索到的信息不足以回答问题，明确告知"根据现有知识库，未找到相关信息"。
            3. 在回答末尾标注信息来源（文件名 + 章节）。
            4. 如果多个来源信息矛盾，请指出矛盾并列出各来源。
            5. 回答要简洁准确，条理清晰。
            
            【检索到的上下文】：
            %s
            
            【用户问题】：%s
            """;

    private final DocumentParser documentParser;
    private final DocumentSplitterConfig splitterConfig;
    private final RedisHybridRetriever retriever;
    final KnowledgeBaseRouter kbRouter;  // package-private，Controller 需访问
    private final EmbeddingModel embeddingModel;
    private final ChatModel chatModel;
    private final RAGProperties ragProperties;

    public RagService(DocumentParser documentParser,
                      DocumentSplitterConfig splitterConfig,
                      RedisHybridRetriever retriever,
                      KnowledgeBaseRouter kbRouter,
                      EmbeddingModel embeddingModel,
                      ChatModel chatModel,
                      RAGProperties ragProperties) {
        this.documentParser = documentParser;
        this.splitterConfig = splitterConfig;
        this.retriever = retriever;
        this.kbRouter = kbRouter;
        this.embeddingModel = embeddingModel;
        this.chatModel = chatModel;
        this.ragProperties = ragProperties;
    }

    /**
     * 文档入库：解析 → 分块 → 向量化 → 写入 Redis
     *
     * @param filePath 文件路径
     * @param kbName  目标知识库名称
     */
    public int ingestDocument(String filePath, String kbName) {
        log.info("开始入库: file={}, kb={}", filePath, kbName);

        // 1. 解析文档
        Document document = documentParser.parseFile(filePath);

        // 2. 智能分块
        List<TextSegment> segments = splitterConfig.splitDocument(document);
        log.info("文档分块完成: {} 个 TextSegment", segments.size());

        // 3. 生成 embedding 并写入 Redis
        var kbProps = kbRouter.getKnowledgeBase(kbName);
        if (kbProps == null) {
            throw new RuntimeException("知识库不存在: " + kbName);
        }

        int successCount = 0;
        for (int i = 0; i < segments.size(); i++) {
            TextSegment segment = segments.get(i);

            try {
                // 生成 embedding
                Embedding embedding = embeddingModel.embed(segment.text()).content();
                float[] vector = embedding.vector();

                // 构建文档 ID
                String docId = kbName + ":" + UUID.nameUUIDFromBytes(
                        (filePath + "_" + i).getBytes()
                ).toString().replace("-", "");

                // 构建元数据
                Map<String, String> metadata = new HashMap<>();
                metadata.put("filename", segment.metadata().containsKey("filename") ? segment.metadata().getString("filename") : "unknown");
                metadata.put("section_title", segment.metadata().containsKey("section_title") ? segment.metadata().getString("section_title") : "未知章节");
                metadata.put("chunk_index", segment.metadata().containsKey("chunk_index") ? segment.metadata().getString("chunk_index") : String.valueOf(i));
                metadata.put("scope", kbProps.getScope());

                // 写入 Redis
                retriever.indexDocument(kbProps.getIndexName(), docId, segment.text(), vector, metadata);
                successCount++;
            } catch (Exception e) {
                log.error("写入 chunk 失败: index={}", i, e);
            }
        }

        log.info("入库完成: file={}, kb={}, 成功 {}/{} 块", filePath, kbName, successCount, segments.size());
        return successCount;
    }

    /**
     * 批量入库目录下所有文档
     */
    public int ingestDirectory(String dirPath, String kbName) {
        List<Document> documents = documentParser.parseDirectory(dirPath);
        int totalSuccess = 0;

        var kbProps = kbRouter.getKnowledgeBase(kbName);
        if (kbProps == null) {
            throw new RuntimeException("知识库不存在: " + kbName);
        }

        for (Document doc : documents) {
            List<TextSegment> segments = splitterConfig.splitDocument(doc);

            for (int i = 0; i < segments.size(); i++) {
                TextSegment segment = segments.get(i);
                try {
                    Embedding embedding = embeddingModel.embed(segment.text()).content();
                    float[] vector = embedding.vector();

                    String docId = kbName + ":" + UUID.nameUUIDFromBytes(
                            (doc.metadata().containsKey("source") ? doc.metadata().getString("source") : "unknown" + "_" + i).getBytes()
                    ).toString().replace("-", "");

                    Map<String, String> metadata = new HashMap<>();
                    metadata.put("filename", segment.metadata().containsKey("filename") ? segment.metadata().getString("filename") : "unknown");
                    metadata.put("section_title", segment.metadata().containsKey("section_title") ? segment.metadata().getString("section_title") : "未知章节");
                    metadata.put("chunk_index", segment.metadata().containsKey("chunk_index") ? segment.metadata().getString("chunk_index") : String.valueOf(i));
                    metadata.put("scope", kbProps.getScope());

                    retriever.indexDocument(kbProps.getIndexName(), docId, segment.text(), vector, metadata);
                    totalSuccess++;
                } catch (Exception e) {
                    log.error("写入失败: chunk {}", i, e);
                }
            }
        }

        log.info("批量入库完成: dir={}, kb={}, 成功 {} 块", dirPath, kbName, totalSuccess);
        return totalSuccess;
    }

    /**
     * RAG 问答：检索 + 生成
     *
     * @param query   用户问题
     * @param kbName  知识库名称（null 表示全库检索）
     * @return RAG 回答
     */
    public RagAnswer ask(String query, String kbName) {
        log.info("RAG 查询: query={}, kb={}", query, kbName);

        // 1. 混合检索
        List<RrfFusion.FusedResult> results;
        if (kbName != null && !kbName.isEmpty()) {
            results = kbRouter.searchSingle(kbName, query, ragProperties.getTopK());
        } else {
            results = kbRouter.searchAll(query, ragProperties.getTopK());
        }

        if (results.isEmpty()) {
            return new RagAnswer("根据现有知识库，未找到相关信息。", Collections.emptyList(), 0.0);
        }

        // 2. 构建上下文
        String context = results.stream()
                .map(r -> {
                    String filename = r.getMetadata() != null ? r.getMetadata().getOrDefault("filename", "未知") : "未知";
                    String section = r.getMetadata() != null ? r.getMetadata().getOrDefault("section_title", "未知") : "未知";
                    return String.format("【来源: %s - %s】\n%s", filename, section, r.text());
                })
                .collect(Collectors.joining("\n\n---\n\n"));

        // 3. 检查相似度
        double maxScore = results.stream()
                .mapToDouble(RrfFusion.FusedResult::getFusedScore)
                .max()
                .orElse(0.0);

        if (maxScore < ragProperties.getMinScore()) {
            log.warn("最高相似度 {} 低于阈值 {}", maxScore, ragProperties.getMinScore());
            return new RagAnswer(
                    "检索到的信息相似度较低，可能不够准确。请尝试更换关键词或补充更多上下文。",
                    results.stream().map(r -> new RetrievalInfo(r.docId(), r.text(), r.getFusedScore())).toList(),
                    maxScore
            );
        }

        // 4. 调用 LLM 生成回答
        String prompt = String.format(SYSTEM_PROMPT, context, query);
        String answer = chatModel.chat(prompt);

        log.info("RAG 回答生成完成, 最高相似度: {}", maxScore);

        List<RetrievalInfo> retrievalInfos = results.stream()
                .map(r -> new RetrievalInfo(r.docId(), r.text(), r.getFusedScore()))
                .toList();

        return new RagAnswer(answer, retrievalInfos, maxScore);
    }

    /**
     * 初始化所有知识库索引
     */
    public void initAllIndexes() {
        kbRouter.initAllIndexes();
    }

    /**
     * 获取知识库路由（供 Controller 使用）
     */
    public KnowledgeBaseRouter getKbRouter() {
        return kbRouter;
    }

    /**
     * RAG 回答
     */
    public record RagAnswer(String answer, List<RetrievalInfo> retrievals, double maxScore) {}

    /**
     * 检索信息
     */
    public record RetrievalInfo(String docId, String text, double score) {}
}
