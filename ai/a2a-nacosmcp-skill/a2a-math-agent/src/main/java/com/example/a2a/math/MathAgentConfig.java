package com.example.a2a.math;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.alibaba.cloud.ai.graph.agent.ReactAgent;

/**
 * 数学智能体配置
 * 功能点：将 ReactAgent 注册为 Bean 后，A2A 框架自动将其注册到 Nacos，
 * 其他服务可通过 A2aRemoteAgent 远程调用
 */
@Configuration
public class MathAgentConfig {

    @Bean
    public ReactAgent mathAgent(ChatModel chatModel, MathTools mathTools) {
        return ReactAgent.builder()
                .name("math_agent")
                .description("专业数学计算智能体，支持加减乘除、幂运算、阶乘等数学运算")
                .model(chatModel)
                .instruction("""
                        你是一个专业的数学助手。
                        职责：
                        1. 解答数学问题，使用计算工具进行精确计算
                        2. 解释计算过程和步骤
                        3. 验证结果的正确性
                        回答要求：
                        - 简洁明了，给出计算步骤
                        - 使用工具进行精确计算，不要心算
                        """)
                .tools(mathTools)
                .build();
    }
}

/**
 * 数学工具类
 * 功能点：通过 @Tool 注解定义可被 Agent 调用的工具函数，
 * 这些工具会自动暴露给远程 A2A 调用方
 */
@Component
class MathTools {

    @Tool(description = "计算两个数的和")
    public double add(
            @ToolParam(description = "第一个加数") double a,
            @ToolParam(description = "第二个加数") double b) {
        return a + b;
    }

    @Tool(description = "计算两个数的差")
    public double subtract(
            @ToolParam(description = "被减数") double a,
            @ToolParam(description = "减数") double b) {
        return a - b;
    }

    @Tool(description = "计算两个数的积")
    public double multiply(
            @ToolParam(description = "第一个因数") double a,
            @ToolParam(description = "第二个因数") double b) {
        return a * b;
    }

    @Tool(description = "计算两个数的商")
    public double divide(
            @ToolParam(description = "被除数") double a,
            @ToolParam(description = "除数，不能为0") double b) {
        if (b == 0) {
            throw new IllegalArgumentException("除数不能为0");
        }
        return a / b;
    }

    @Tool(description = "计算 a 的 b 次幂")
    public double power(
            @ToolParam(description = "底数") double a,
            @ToolParam(description = "指数") double b) {
        return Math.pow(a, b);
    }

    @Tool(description = "计算 n 的阶乘")
    public long factorial(
            @ToolParam(description = "非负整数 n") int n) {
        if (n < 0) {
            throw new IllegalArgumentException("阶乘参数不能为负数");
        }
        long result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }
}