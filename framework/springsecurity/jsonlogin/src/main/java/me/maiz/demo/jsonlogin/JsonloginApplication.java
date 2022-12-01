package me.maiz.demo.jsonlogin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("me.maiz.demo.jsonlogin.mapper")
public class JsonloginApplication {

    public static void main(String[] args) {
        SpringApplication.run(JsonloginApplication.class, args);
    }

}
