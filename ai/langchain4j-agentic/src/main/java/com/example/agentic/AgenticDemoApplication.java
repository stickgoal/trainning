package com.example.agentic;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

/**
 * 注意：@MapperScan 只扫真正的 MyBatis Mapper 接口。
 * 极简教学模块 {@code com.example.agentic.hitlsimple} 里有个 LangChain4j 的
 * AiServices 接口 {@code HitlAgent}，若被 MyBatis 当成 Mapper 扫进去，会和我们
 * 在 HitlAgentConfig 里 @Bean 定义的同名 bean 冲突（ConflictingBeanDefinitionException）。
 * 因此这里把它从 Mapper 扫描里排除掉——它属于 Agent 层，不是数据访问层。
 */
@MapperScan(
        basePackages = {"com.example.agentic.common.mapper", "com.example.agentic.humanintheloop.mapper"},
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.REGEX,
                pattern = "com\\.example\\.agentic\\.hitlsimple\\..*"
        )
)
@SpringBootApplication
public class AgenticDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgenticDemoApplication.class, args);
    }
}
