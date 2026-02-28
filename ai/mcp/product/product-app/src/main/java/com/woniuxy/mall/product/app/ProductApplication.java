package com.woniuxy.mall.product.app;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "com.woniuxy.mall.product")
@EnableFeignClients(basePackages = "com.woniuxy.mall.*.client")
@MapperScan("com.woniuxy.mall.product.infra.mapper")
public class ProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class, args);
    }

}
