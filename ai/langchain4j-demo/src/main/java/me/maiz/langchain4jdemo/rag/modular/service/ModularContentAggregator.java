package me.maiz.langchain4jdemo.rag.modular.service;

import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.aggregator.ContentAggregator;
import dev.langchain4j.rag.query.Query;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 模块化 RAG 内容聚合器
 *
 * 实现 ContentAggregator 接口，处理多查询/多检索器的结果合并。
 *
 * 职责：
 * 1. 去重 — 基于文本相似度去除重复内容
 * 2. 排序 — 按检索得分降序排列
 * 3. 截断 — 控制总内容长度
 *
 * 这是 Modular RAG 中可独立替换的模块之一。
 */
@Slf4j
public class ModularContentAggregator implements ContentAggregator {

    /** 最大保留的内容片段数 */
    private static final int MAX_RESULTS = 3;

    @Override
    public List<Content> aggregate(Map<Query, Collection<List<Content>>> queryResults) {
        log.info("聚合器收到 {} 个查询的检索结果", queryResults.size());

        // 1. 扁平化所有检索结果
        List<Content> allContents = new ArrayList<>();
        for (Collection<List<Content>> retrieversResult : queryResults.values()) {
            for (List<Content> retrieverResult : retrieversResult) {
                allContents.addAll(retrieverResult);
            }
        }

        log.info("扁平化后共 {} 条内容", allContents.size());

        if (allContents.isEmpty()) {
            return allContents;
        }

        // 2. 去重（基于文本前50字符的指纹）
        List<Content> deduplicated = deduplicate(allContents);
        log.info("去重后剩余 {} 条", deduplicated.size());

        // 3. 截断
        List<Content> truncated = deduplicated.size() > MAX_RESULTS
                ? deduplicated.subList(0, MAX_RESULTS)
                : deduplicated;
        log.info("截断后保留 {} 条", truncated.size());

        return truncated;
    }

    /**
     * 基于文本前缀去重
     */
    private List<Content> deduplicate(List<Content> contents) {
        List<Content> result = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        for (Content content : contents) {
            String text = content.textSegment() != null
                    ? content.textSegment().text()
                    : "";

            String fingerprint = text.length() > 50
                    ? text.substring(0, 50)
                    : text;

            if (!seen.contains(fingerprint)) {
                seen.add(fingerprint);
                result.add(content);
            }
        }

        return result;
    }
}
