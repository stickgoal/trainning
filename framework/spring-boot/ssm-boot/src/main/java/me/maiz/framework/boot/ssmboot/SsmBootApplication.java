package me.maiz.framework.boot.ssmboot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("me.maiz.framework.boot.ssmboot.dal")
public class SsmBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(SsmBootApplication.class, args);
    }

}
