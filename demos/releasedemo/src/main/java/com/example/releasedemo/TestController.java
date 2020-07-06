package com.example.releasedemo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Value("${spring.datasource.url}")
    private String url;

    @GetMapping("t")
    public String t(){
        return url;
    }

}
