package me.maiz.langchain4jdemo.rag.advanced.custom;

import dev.langchain4j.community.store.embedding.redis.RedisEmbeddingStore;
import dev.langchain4j.community.store.embedding.redis.spring.RedisEmbeddingStoreProperties;
import dev.langchain4j.model.embedding.EmbeddingModel;
import lombok.Builder;
import org.springframework.beans.factory.ObjectProvider;
import redis.clients.jedis.UnifiedJedis;
import redis.clients.jedis.search.schemafields.SchemaField;

import java.util.Map;
import java.util.Optional;

public class CustomRedisEmbeddingStore  {
    private  RedisEmbeddingStore redisEmbeddingStore;

    public CustomRedisEmbeddingStore(RedisEmbeddingStoreProperties properties, ObjectProvider<EmbeddingModel> embeddingModelProvider, ObjectProvider<UnifiedJedis> unifiedJedisProvider,String prefix)  {
        RedisEmbeddingStore embeddingStore = RedisEmbeddingStore.builder()
                .unifiedJedis((UnifiedJedis) unifiedJedisProvider.getIfAvailable())
                .prefix(prefix)
                .indexName(properties.getIndexName())
                .dimension((Integer) Optional.ofNullable((EmbeddingModel) embeddingModelProvider.getIfAvailable())
                        .map(EmbeddingModel::dimension).orElse(properties.getDimension()))
                .metadataKeys(properties.getMetadataKeys())
                .build();
        this.redisEmbeddingStore = embeddingStore;
    }

    public RedisEmbeddingStore getRedisEmbeddingStore() {
        return redisEmbeddingStore;
    }
}
