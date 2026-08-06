package com.yourproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * RAG-Ready 企业级 RAG 系统启动类
 * 基于 Redis Stack + LangChain4j
 */
@SpringBootApplication
public class RagReadyApplication {

    public static void main(String[] args) {
        SpringApplication.run(RagReadyApplication.class, args);
    }
}
