package com.woniuxy.esquickstart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@SpringBootApplication
@EnableElasticsearchRepositories
public class EsquickstartApplication {

    public static void main(String[] args) {
        SpringApplication.run(EsquickstartApplication.class, args);
    }

}
