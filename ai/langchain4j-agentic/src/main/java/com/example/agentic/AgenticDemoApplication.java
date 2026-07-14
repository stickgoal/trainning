package com.example.agentic;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.example.agentic")
@SpringBootApplication
public class AgenticDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgenticDemoApplication.class, args);
    }
}
