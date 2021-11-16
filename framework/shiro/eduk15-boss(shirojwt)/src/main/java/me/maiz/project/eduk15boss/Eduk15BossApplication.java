package me.maiz.project.eduk15boss;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("me.maiz.project.eduk15boss.dao")
public class Eduk15BossApplication {

    public static void main(String[] args) {
        SpringApplication.run(Eduk15BossApplication.class, args);
    }

}
