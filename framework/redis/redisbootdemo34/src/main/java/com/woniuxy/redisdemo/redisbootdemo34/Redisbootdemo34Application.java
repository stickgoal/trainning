package com.woniuxy.redisdemo.redisbootdemo34;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootApplication
@EnableCaching
public class Redisbootdemo34Application {

    public static void main(String[] args) {
        SpringApplication.run(Redisbootdemo34Application.class, args);
    }

}
