package me.maiz.demo.mybatisplusdemo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("me.maiz.demo.mybatisplusdemo.mapper")
public class MybatisplusdemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MybatisplusdemoApplication.class, args);
	}
}
