package me.maiz.project.highconcurrency;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@MapperScan("me.maiz.project.highconcurrency.dao")
@EnableCaching
public class HighconcurrencyApplication {

	public static void main(String[] args) {
		SpringApplication.run(HighconcurrencyApplication.class, args);
	}

}
