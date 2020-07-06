package com.example.releasedemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ReleasedemoApplication {

    public static void main(String[] args) {
//        System.setProperty("spring.profiles.active","prod");
        SpringApplication.run(ReleasedemoApplication.class, args);
    }

}
