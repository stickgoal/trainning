package com.yourproject.retrieval;

import com.yourproject.config.KnowledgeBaseConfig;
import com.yourproject.config.KnowledgeBaseConfig.KnowledgeBaseProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 多知识库路由
 * 支持多知识库并行检索 + 结果融合
 */
@Component
public class KnowledgeBaseRouter {

    private static final Logger log = LoggerFactory.getLogger(KnowledgeBaseRouter.class);

    private final KnowledgeBaseConfig kbConfig;
    private final RedisHybridRetriever retriever;

    public KnowledgeBaseRouter(KnowledgeBaseConfig kbConfig, RedisHybridRetriever retriever) {
        this.kbConfig = kbConfig;
        this.retriever = retriever;
    }

    /**
     * 单知识库检索
     */
    public List<RrfFusion.FusedResult> searchSingle(String kbName, String query, int topK) {
        KnowledgeBaseProperties props = kbConfig.getBases().get(kbName);
        if (props == null) {
            log.warn("知识库不存在: {}", kbName);
            return Collections.emptyList();
        }
        return retriever.hybridSearch(props.getIndexName(), query, topK);
    }

    /**
     * 多知识库并行检索 + RRF 融合
     *
     * @param kbNames 知识库名称列表
     * @param query   查询文本
     * @param topK    每个知识库返回的数量
     * @return 融合后的结果
     */
    public List<RrfFusion.FusedResult> searchMultiple(List<String> kbNames, String query, int topK) {
        log.info("多知识库并行检索: KBs={}, query={}, topK={}", kbNames, query, topK);

        // 并行检索
        List<CompletableFuture<List<RrfFusion.FusedResult>>> futures = kbNames.stream()
                .map(kbName -> CompletableFuture.supplyAsync(() -> {
                    try {
                        return searchSingle(kbName, query, topK);
                    } catch (Exception e) {
                        log.error("知识库检索失败: {}", kbName, e);
                        return Collections.<RrfFusion.FusedResult>emptyList();
                    }
                }))
                .collect(Collectors.toList());

        // 等待所有检索完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // 收集所有结果
        List<List<RrfFusion.RetrievalResult>> allResults = new ArrayList<>();
        for (CompletableFuture<List<RrfFusion.FusedResult>> future : futures) {
            List<RrfFusion.FusedResult> results = future.join();
            // 将 FusedResult 转换为 RetrievalResult 用于二次融合
            List<RrfFusion.RetrievalResult> retrievalResults = results.stream()
                    .map(fused -> new RrfFusion.RetrievalResult(
                            fused.docId(),
                            fused.text(),
                            fused.getFusedScore(),
                            fused.getMetadata() != null ? fused.getMetadata() : new HashMap<>()
                    ))
                    .collect(Collectors.toList());
            allResults.add(retrievalResults);
        }

        // 二次 RRF 融合
        RrfFusion fusion = new RrfFusion(60);
        List<RrfFusion.FusedResult> finalResults = fusion.fuse(allResults);

        // 截取 topK
        if (finalResults.size() > topK) {
            finalResults = finalResults.subList(0, topK);
        }

        log.info("多知识库检索完成: 融合后 {} 条结果", finalResults.size());
        return finalResults;
    }

    /**
     * 全知识库检索
     */
    public List<RrfFusion.FusedResult> searchAll(String query, int topK) {
        List<String> allKbNames = new ArrayList<>(kbConfig.getBases().keySet());
        return searchMultiple(allKbNames, query, topK);
    }

    /**
     * 初始化所有知识库索引
     */
    public void initAllIndexes() {
        for (Map.Entry<String, KnowledgeBaseProperties> entry : kbConfig.getBases().entrySet()) {
            String kbName = entry.getKey();
            KnowledgeBaseProperties props = entry.getValue();
            log.info("初始化知识库索引: {} -> {}", kbName, props.getIndexName());
            retriever.createIndex(props.getIndexName(), props.getDimension());
        }
    }

    /**
     * 获取所有知识库名称
     */
    public Set<String> getKnowledgeBaseNames() {
        return kbConfig.getBases().keySet();
    }

    /**
     * 获取知识库配置
     */
    public KnowledgeBaseProperties getKnowledgeBase(String name) {
        return kbConfig.getBases().get(name);
    }
}
