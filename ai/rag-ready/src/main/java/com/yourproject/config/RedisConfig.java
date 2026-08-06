package com.yourproject.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.UnifiedJedis;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisClientConfig;

import java.time.Duration;

/**
 * Redis Stack 连接池配置
 * 使用 Jedis 客户端连接 Redis Stack，支持 RediSearch 和 RedisJSON
 */
@Configuration
public class RedisConfig {

    private static final Logger log = LoggerFactory.getLogger(RedisConfig.class);

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.password}")
    private String password;

    @Value("${spring.data.redis.timeout}")
    private Duration timeout;

    /**
     * Jedis 连接池（线程安全）
     * 用于执行 RediSearch 命令（FT.CREATE, FT.SEARCH 等）
     */
    @Bean(destroyMethod = "close")
    public JedisPooled jedisPooled() {
        log.info("初始化 JedisPooled 连接池: {}:{}，超时: {}ms", host, port, timeout.toMillis());
        JedisClientConfig config = DefaultJedisClientConfig.builder()
                .password(password)
                .timeoutMillis((int) timeout.toMillis())
                .build();
        return new JedisPooled(new HostAndPort(host, port), config);
    }

    /**
     * UnifiedJedis 实例（支持 RediSearch 模块命令）
     */
    @Bean
    public UnifiedJedis unifiedJedis(JedisPooled jedisPooled) {
        return jedisPooled;
    }
}
