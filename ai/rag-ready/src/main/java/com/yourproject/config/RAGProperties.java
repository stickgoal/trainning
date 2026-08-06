package com.yourproject.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * RAG 调优参数配置
 * 包含分块、检索、融合等可调参数
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "rag")
public class RAGProperties {

    /** 分块大小（token 数） */
    private int chunkSize = 300;
    /** 重叠 token 数 */
    private int overlapTokens = 50;
    /** 检索 top-K */
    private int topK = 5;
    /** 最低相似度阈值 */
    private double minScore = 0.6;
    /** HNSW 参数 */
    private HnswConfig hnsw = new HnswConfig();
    /** RRF 融合参数 */
    private RrfConfig rrf = new RrfConfig();
    /** 混合检索权重 */
    private HybridConfig hybrid = new HybridConfig();

    @Data
    public static class HnswConfig {
        private int efConstruction = 200;
        private int m = 16;
        private int efSearch = 10;
    }

    @Data
    public static class RrfConfig {
        private int k = 60;
    }

    @Data
    public static class HybridConfig {
        /** 向量检索权重 (0-1) */
        private double vectorWeight = 0.7;
        /** BM25 检索权重 (0-1) */
        private double bm25Weight = 0.3;
    }
}
