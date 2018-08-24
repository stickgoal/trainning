package me.maiz.demo.moderntech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ModerntechApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModerntechApplication.class, args);
    }
}
