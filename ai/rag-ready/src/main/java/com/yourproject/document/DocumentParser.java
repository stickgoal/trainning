package com.yourproject.document;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * 多格式文档解析器
 * 基于 Apache Tika 支持 PDF、Word、Excel、Markdown、TXT、HTML
 * 解析后生成 LangChain4j 标准 Document 对象，携带元数据
 */
@Component
public class DocumentParser {

    private static final Logger log = LoggerFactory.getLogger(DocumentParser.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 解析单个文件为 Document
     *
     * @param filePath 文件路径
     * @return LangChain4j Document 对象
     */
    public Document parseFile(String filePath) {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new RuntimeException("文件不存在: " + filePath);
        }

        log.info("开始解析文件: {}", path.getFileName());

        try (InputStream is = Files.newInputStream(path)) {
            // 使用 LangChain4j 的 Tika 解析器
            ApacheTikaDocumentParser tikaParser = new ApacheTikaDocumentParser();
            Document doc = tikaParser.parse(is);

            // 构建元数据
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("filename", path.getFileName().toString());
            metadata.put("source", path.toString());
            metadata.put("timestamp", LocalDateTime.now().format(FORMATTER));
            metadata.put("file_size", Files.size(path));

            // 尝试提取章节标题（首行或前 100 字符）
            String text = doc.text();
            String sectionTitle = extractSectionTitle(text);
            metadata.put("section_title", sectionTitle);

            // 合并 Tika 解析出的元数据
            doc.metadata().toMap().forEach((k, v) -> {
                if (v != null && !v.toString().isEmpty()) {
                    metadata.putIfAbsent(k, v);
                }
            });

            return Document.from(text, dev.langchain4j.data.document.Metadata.from(metadata));

        } catch (Exception e) {
            log.error("解析文件失败: {}", filePath, e);
            throw new RuntimeException("解析文件失败: " + filePath, e);
        }
    }

    /**
     * 批量解析目录下所有支持的文档
     *
     * @param dirPath 目录路径
     * @return Document 列表
     */
    public List<Document> parseDirectory(String dirPath) {
        List<Document> documents = new ArrayList<>();
        Path dir = Paths.get(dirPath);

        if (!Files.isDirectory(dir)) {
            log.warn("目录不存在或不是目录: {}", dirPath);
            return documents;
        }

        try (Stream<Path> paths = Files.walk(dir)) {
            paths.filter(Files::isRegularFile)
                    .filter(p -> isSupportedFormat(p.toString()))
                    .forEach(p -> {
                        try {
                            Document doc = parseFile(p.toString());
                            documents.add(doc);
                            log.info("成功解析: {} ({} 字符)", p.getFileName(), doc.text().length());
                        } catch (Exception e) {
                            log.error("跳过文件: {} - {}", p.getFileName(), e.getMessage());
                        }
                    });
        } catch (Exception e) {
            log.error("遍历目录失败: {}", dirPath, e);
        }

        log.info("目录解析完成: {}，共解析 {} 个文档", dirPath, documents.size());
        return documents;
    }

    /**
     * 从文本中提取章节标题
     * 策略：取第一个非空行，如果长度 < 50 则作为标题
     */
    private String extractSectionTitle(String text) {
        if (text == null || text.isEmpty()) {
            return "未知章节";
        }
        String[] lines = text.split("\\r?\\n");
        for (String line : lines) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty() && trimmed.length() <= 50) {
                return trimmed;
            }
            if (!trimmed.isEmpty()) {
                return trimmed.substring(0, Math.min(50, trimmed.length()));
            }
        }
        return "未知章节";
    }

    /**
     * 检查文件格式是否支持
     */
    private boolean isSupportedFormat(String filename) {
        String lower = filename.toLowerCase();
        return lower.endsWith(".pdf") ||
               lower.endsWith(".docx") || lower.endsWith(".doc") ||
               lower.endsWith(".xlsx") || lower.endsWith(".xls") ||
               lower.endsWith(".md") || lower.endsWith(".markdown") ||
               lower.endsWith(".txt") ||
               lower.endsWith(".html") || lower.endsWith(".htm");
    }
}
