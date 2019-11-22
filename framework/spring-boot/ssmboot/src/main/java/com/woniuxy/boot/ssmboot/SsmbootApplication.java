package com.woniuxy.boot.ssmboot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan("com.woniuxy.boot.ssmboot.dao")
//@EnableTransactionManagement
public class SsmbootApplication {

    public static void main(String[] args) {
        SpringApplication.run(SsmbootApplication.class, args);
    }

}
