package me.maiz.shardingjdbcreadwrite;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("me.maiz.shardingjdbcreadwrite.mapper")
public class ShardingJdbcReadwriteApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShardingJdbcReadwriteApplication.class, args);
    }

}
