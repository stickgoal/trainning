package com.example.a2a.client;

import com.alibaba.cloud.ai.graph.agent.A2aRemoteAgent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * A2A 客户端控制器
 * 功能点：通过 REST API 演示 A2A 远程 Agent 调用
 * 
 * 调用链：用户请求 → A2A Client → Nacos 服务发现 → 远程 Agent 服务 → 返回结果
 */
@RestController
@RequestMapping("/api/a2a")
public class A2aController {

    private final A2aRemoteAgent remoteMathAgent;
    private final A2aRemoteAgent remoteWeatherAgent;

    public A2aController(A2aRemoteAgent remoteMathAgent, A2aRemoteAgent remoteWeatherAgent) {
        this.remoteMathAgent = remoteMathAgent;
        this.remoteWeatherAgent = remoteWeatherAgent;
    }

    /**
     * 调用远程数学智能体
     * 示例：GET /api/a2a/math?question=计算 15 乘以 32 再加上 100
     */
    @GetMapping("/math")
    public String callMathAgent(@RequestParam String question) {
        Optional<com.alibaba.cloud.ai.graph.OverAllState> result = remoteMathAgent.invoke(question);
        return result.map(state -> {
            Object value = state.value("messages");
            return value != null ? value.toString() : "无返回结果";
        }).orElse("远程数学智能体调用失败");
    }

    /**
     * 调用远程天气智能体
     * 示例：GET /api/a2a/weather?question=查询北京和上海的天气
     */
    @GetMapping("/weather")
    public String callWeatherAgent(@RequestParam String question) {
        Optional<com.alibaba.cloud.ai.graph.OverAllState> result = remoteWeatherAgent.invoke(question);
        return result.map(state -> {
            Object value = state.value("messages");
            return value != null ? value.toString() : "无返回结果";
        }).orElse("远程天气智能体调用失败");
    }

    /**
     * 综合查询：同时调用两个远程 Agent
     * 示例：GET /api/a2a/combined?question=北京今天天气怎么样？如果温度超过25度，帮我算一下华氏温度
     */
    @GetMapping("/combined")
    public String callCombined(@RequestParam String question) {
        StringBuilder response = new StringBuilder();

        // 先调用天气 Agent 获取天气信息
        response.append("【天气查询结果】\n");
        Optional<com.alibaba.cloud.ai.graph.OverAllState> weatherResult = remoteWeatherAgent.invoke(question);
        String weatherInfo = weatherResult.map(state -> {
            Object value = state.value("messages");
            return value != null ? value.toString() : "无返回结果";
        }).orElse("天气查询失败");
        response.append(weatherInfo).append("\n\n");

        // 再调用数学 Agent 进行温度转换
        response.append("【数学计算结果】\n");
        Optional<com.alibaba.cloud.ai.graph.OverAllState> mathResult = remoteMathAgent.invoke(
                "如果北京今天25摄氏度，请帮我转换为华氏度（公式：F = C × 9/5 + 32）");
        String mathInfo = mathResult.map(state -> {
            Object value = state.value("messages");
            return value != null ? value.toString() : "无返回结果";
        }).orElse("数学计算失败");
        response.append(mathInfo);

        return response.toString();
    }
}