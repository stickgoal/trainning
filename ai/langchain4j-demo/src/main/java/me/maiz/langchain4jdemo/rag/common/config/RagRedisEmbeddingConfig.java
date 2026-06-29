package me.maiz.langchain4jdemo.rag.common.config;

import dev.langchain4j.community.store.embedding.redis.RedisEmbeddingStore;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Redis 向量存储共享配置
 *
 * 使用 langchain4j-community-redis 的 RedisEmbeddingStore 替代 InMemoryEmbeddingStore，
 * 使向量数据持久化到 redis-stack（需含 RediSearch 向量检索模块）。
 *
 * 连接参数与维度通过 application.yaml 的 rag.redis.* 注入，便于各 RAG 层次复用。
 * 维度必须与所用 EmbeddingModel 输出一致（DashScope text-embedding-v3 = 1024），
 * 索引不存在时会在首次写入时按该维度自动创建。
 */
@Configuration
public class RagRedisEmbeddingConfig {

    @Bean
    @Primary
    public EmbeddingStore<TextSegment> redisEmbeddingStore(
            @Value("${rag.redis.host}") String host,
            @Value("${rag.redis.port}") int port,
            @Value("${rag.redis.index-name}") String indexName,
            @Value("${rag.redis.prefix}") String prefix,
            @Value("${rag.redis.dimension}") int dimension) {
        return RedisEmbeddingStore.builder()
                .host(host)
                .port(port)
                .indexName(indexName)
                .prefix(prefix)
                .dimension(dimension)
                .build();
    }
}
