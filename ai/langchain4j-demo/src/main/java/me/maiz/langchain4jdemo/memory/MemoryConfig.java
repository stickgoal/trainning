package me.maiz.langchain4jdemo.memory;

import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import dev.langchain4j.community.store.memory.chat.redis.StoreType;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import dev.langchain4j.store.memory.chat.InMemoryChatMemoryStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Configuration
public class MemoryConfig {

    @Bean
    public ChatMemoryProvider chatMemoryProvider() {
        return memoryId ->
                MessageWindowChatMemory.builder()
                .id(memoryId==null?new Random().nextInt():memoryId)
                .maxMessages(10)
                .chatMemoryStore(redisStore()) // 注入Redis存储
                .build();
    }

    @Bean
    public ChatMemoryStore redisStore() {
        return RedisChatMemoryStore.builder().host("127.0.0.1").port(6379).storeType(StoreType.STRING).prefix("chat:memory:").build();
    }
}
