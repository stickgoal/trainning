package me.maiz.security.withddb;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("me.maiz.security.withddb.mapper")
public class WithddbApplication {

    public static void main(String[] args) {
        SpringApplication.run(WithddbApplication.class, args);
    }

}
