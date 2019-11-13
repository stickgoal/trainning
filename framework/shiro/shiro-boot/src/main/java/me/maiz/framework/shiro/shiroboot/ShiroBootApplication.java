package me.maiz.framework.shiro.shiroboot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("me.maiz.framework.shiro.shiroboot.dao")
public class ShiroBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShiroBootApplication.class, args);
    }

}
