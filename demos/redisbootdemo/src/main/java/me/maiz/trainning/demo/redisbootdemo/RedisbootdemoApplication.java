package me.maiz.trainning.demo.redisbootdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class RedisbootdemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisbootdemoApplication.class, args);
    }

}
