package com.example.mcp.calc;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

/**
 * MCP 计算器服务
 * 功能点：通过 @Tool 注解暴露数学计算工具，自动注册到 Nacos MCP Registry
 * 与 A2A 的 MathAgent 形成对比：MCP 是工具级注册，A2A 是 Agent 级注册
 */
@Service
public class CalculatorMcpService {

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

    @Tool(description = "计算平方根")
    public double sqrt(
            @ToolParam(description = "被开方数，必须非负") double x) {
        if (x < 0) {
            throw new IllegalArgumentException("被开方数不能为负数");
        }
        return Math.sqrt(x);
    }
}