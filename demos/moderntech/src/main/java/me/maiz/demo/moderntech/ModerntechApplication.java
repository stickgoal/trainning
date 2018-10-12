package me.maiz.demo.moderntech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class ModerntechApplication {

    public static void main(String[] args) {

        SpringApplication.run(ModerntechApplication.class, args);

    }
}
