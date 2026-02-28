package com.wn.demomcpserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.woniuxy.mall.product.client")
public class DemoMcpServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoMcpServerApplication.class, args);
    }

}
