package com.yourproject.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 知识库配置属性
 * 支持多知识库管理，每个知识库有独立的 Redis 索引
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "knowledge-base")
public class KnowledgeBaseConfig {

    /**
     * 知识库映射：key = 知识库名称，value = 知识库配置
     */
    private Map<String, KnowledgeBaseProperties> bases = new HashMap<>();

    @Data
    public static class KnowledgeBaseProperties {
        /** Redis 索引名，如 idx:kb_hr */
        private String indexName;
        /** 知识库 scope 标签 */
        private String scope;
        /** 知识库描述 */
        private String description;
        /** Embedding 维度 */
        private int dimension;
    }
}
