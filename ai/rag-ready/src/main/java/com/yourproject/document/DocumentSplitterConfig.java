package com.yourproject.document;

import com.yourproject.config.RAGProperties;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 智能文本分块配置
 * 使用 LangChain4j 的递归分块策略，支持动态参数调整
 *
 * 分块策略：
 * 1. 优先按段落/标题切分
 * 2. Token 级别控制（chunkSize + overlapTokens）
 * 3. 每个 TextSegment 继承父文档元数据，追加 chunk_index 和 section_title
 */
@Component
public class DocumentSplitterConfig {

    private static final Logger log = LoggerFactory.getLogger(DocumentSplitterConfig.class);

    private final RAGProperties ragProperties;
    private final EmbeddingModel embeddingModel;

    public DocumentSplitterConfig(RAGProperties ragProperties, EmbeddingModel embeddingModel) {
        this.ragProperties = ragProperties;
        this.embeddingModel = embeddingModel;
    }

    /**
     * 使用递归分块策略切分文档
     * 递归策略：先按段落 → 再按句子 → 最后按 token
     *
     * @param document 待切分的文档
     * @return TextSegment 列表
     */
    public List<TextSegment> splitDocument(Document document) {
        int chunkSize = ragProperties.getChunkSize();
        int overlap = ragProperties.getOverlapTokens();

        log.info("分块参数: chunkSize={}, overlap={}", chunkSize, overlap);

        // 使用 LangChain4j 递归分块器
        // 递归策略会先尝试按段落分，段落太大再按句子，最后按 token
        List<TextSegment> segments = splitRecursively(document, chunkSize, overlap);

        // 为每个 segment 追加 chunk_index
        List<TextSegment> indexedSegments = new ArrayList<>();
        String sectionTitle = document.metadata().containsKey("section_title") ? document.metadata().getString("section_title") : "未知章节";

        for (int i = 0; i < segments.size(); i++) {
            TextSegment segment = segments.get(i);
            // 在 metadata 中追加 chunk_index
            dev.langchain4j.data.document.Metadata updatedMeta = segment.metadata();
            updatedMeta.put("chunk_index", String.valueOf(i));
            updatedMeta.put("section_title", sectionTitle);
            updatedMeta.put("total_chunks", String.valueOf(segments.size()));

            TextSegment updated = TextSegment.from(segment.text(), updatedMeta);
            indexedSegments.add(updated);
        }

        log.info("文档分块完成: 原始长度={} 字符, 分块数={}", document.text().length(), indexedSegments.size());
        return indexedSegments;
    }

    /**
     * 批量分块
     */
    public List<TextSegment> splitDocuments(List<Document> documents) {
        List<TextSegment> allSegments = new ArrayList<>();
        for (Document doc : documents) {
            allSegments.addAll(splitDocument(doc));
        }
        log.info("批量分块完成: {} 个文档 → {} 个 TextSegment", documents.size(), allSegments.size());
        return allSegments;
    }

    /**
     * 递归分块实现
     * 手动实现段落 → 句子 → token 的递归切分
     *
     * @param document 文档
     * @param maxTokens 最大 token 数
     * @param overlap 重叠 token 数
     * @return TextSegment 列表
     */
    private List<TextSegment> splitRecursively(Document document, int maxTokens, int overlap) {
        List<TextSegment> result = new ArrayList<>();
        String text = document.text();

        if (text == null || text.trim().isEmpty()) {
            return result;
        }

        // 第一层：按段落（双换行）切分
        String[] paragraphs = text.split("\\n\\s*\\n");

        StringBuilder currentChunk = new StringBuilder();
        int currentTokens = 0;

        for (String paragraph : paragraphs) {
            String trimmedPara = paragraph.trim();
            if (trimmedPara.isEmpty()) continue;

            int paraTokens = estimateTokens(trimmedPara);

            // 如果当前段落本身超过 maxTokens，需要二次切分
            if (paraTokens > maxTokens) {
                // 先保存当前 chunk
                if (currentChunk.length() > 0) {
                    result.add(createSegment(currentChunk.toString().trim(), document));
                    currentChunk = new StringBuilder();
                    currentTokens = 0;
                }
                // 按句子切分超长段落
                result.addAll(splitBySentence(trimmedPara, maxTokens, overlap, document));
            } else if (currentTokens + paraTokens > maxTokens) {
                // 当前 chunk 加上这段会超限，先保存
                if (currentChunk.length() > 0) {
                    result.add(createSegment(currentChunk.toString().trim(), document));
                    // 保留 overlap：取上一个 chunk 的末尾
                    String overlapText = getOverlapText(currentChunk.toString(), overlap);
                    currentChunk = new StringBuilder(overlapText);
                    currentTokens = estimateTokens(overlapText);
                }
                currentChunk.append(trimmedPara).append("\n\n");
                currentTokens += paraTokens;
            } else {
                currentChunk.append(trimmedPara).append("\n\n");
                currentTokens += paraTokens;
            }
        }

        // 保存最后一个 chunk
        if (currentChunk.length() > 0) {
            result.add(createSegment(currentChunk.toString().trim(), document));
        }

        return result;
    }

    /**
     * 按句子切分超长段落
     */
    private List<TextSegment> splitBySentence(String paragraph, int maxTokens, int overlap, Document document) {
        List<TextSegment> result = new ArrayList<>();
        // 按中文句号、英文句号、问号、感叹号切分
        String[] sentences = paragraph.split("(?<=[。.!?！？])\\s*");

        StringBuilder currentChunk = new StringBuilder();
        int currentTokens = 0;

        for (String sentence : sentences) {
            String trimmed = sentence.trim();
            if (trimmed.isEmpty()) continue;

            int sentTokens = estimateTokens(trimmed);

            if (currentTokens + sentTokens > maxTokens && currentChunk.length() > 0) {
                result.add(createSegment(currentChunk.toString().trim(), document));
                String overlapText = getOverlapText(currentChunk.toString(), overlap);
                currentChunk = new StringBuilder(overlapText);
                currentTokens = estimateTokens(overlapText);
            }

            currentChunk.append(trimmed);
            currentTokens += sentTokens;
        }

        if (currentChunk.length() > 0) {
            result.add(createSegment(currentChunk.toString().trim(), document));
        }

        return result;
    }

    /**
     * 获取 chunk 末尾的 overlap 文本
     */
    private String getOverlapText(String text, int overlapTokens) {
        if (text == null || text.isEmpty()) return "";
        // 简单实现：取末尾 overlapTokens * 4 个字符（粗略估计 1 token ≈ 4 字符）
        int charCount = overlapTokens * 4;
        int start = Math.max(0, text.length() - charCount);
        // 找到最近的句子边界
        String suffix = text.substring(start);
        int sentenceStart = suffix.indexOf("。");
        if (sentenceStart == -1) sentenceStart = suffix.indexOf(". ");
        if (sentenceStart >= 0 && sentenceStart < suffix.length() - 1) {
            return suffix.substring(sentenceStart + 1).trim();
        }
        return suffix.trim();
    }

    /**
     * 粗略估算 token 数
     * 中文约 1 字 ≈ 1 token，英文约 4 字符 ≈ 1 token
     */
    private int estimateTokens(String text) {
        if (text == null || text.isEmpty()) return 0;
        int chineseChars = 0;
        int nonChineseChars = 0;
        for (char c : text.toCharArray()) {
            if (Character.UnicodeScript.of(c) == Character.UnicodeScript.HAN) {
                chineseChars++;
            } else {
                nonChineseChars++;
            }
        }
        return chineseChars + nonChineseChars / 4;
    }

    /**
     * 创建 TextSegment，继承父文档元数据
     */
    private TextSegment createSegment(String text, Document document) {
        // 复制父文档的元数据
        dev.langchain4j.data.document.Metadata meta = document.metadata();
        return TextSegment.from(text, meta);
    }
}
