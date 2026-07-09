package com.example.mcp.common.service;

import com.example.mcp.common.model.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 模拟数据服务，提供天气查询、新闻查询、文件查询、数学计算的模拟数据。
 * 这些数据主要供 MCP Server 和本地工具使用。
 */
@Service
public class MockDataService {

    private final Map<String, WeatherInfo> weatherData = Map.of(
        "北京", WeatherInfo.builder()
            .city("北京").temperature(22.5).condition("晴")
            .humidity(45.0).windSpeed(3.2).build(),
        "上海", WeatherInfo.builder()
            .city("上海").temperature(26.0).condition("多云")
            .humidity(68.0).windSpeed(4.5).build(),
        "广州", WeatherInfo.builder()
            .city("广州").temperature(30.0).condition("雷阵雨")
            .humidity(82.0).windSpeed(6.0).build(),
        "深圳", WeatherInfo.builder()
            .city("深圳").temperature(29.5).condition("晴")
            .humidity(70.0).windSpeed(5.0).build()
    );

    private final List<NewsArticle> newsData = List.of(
        NewsArticle.builder()
            .title("AI技术峰会2025在上海召开")
            .content("2025年AI技术峰会在上海隆重召开，众多AI领域专家齐聚一堂...")
            .source("科技日报")
            .publishedAt(LocalDateTime.of(2025, 6, 15, 9, 0)).build(),
        NewsArticle.builder()
            .title("LangChain4j发布1.16版本")
            .content("LangChain4j正式发布1.16版本，新增MCP支持和Agentic API...")
            .source("开源中国")
            .publishedAt(LocalDateTime.of(2025, 6, 20, 14, 30)).build()
    );

    private final Map<String, FileInfo> fileData = Map.of(
        "readme.txt", FileInfo.builder()
            .fileName("readme.txt").fileSize(1024).fileType("text/plain")
            .content("这是一个示例文件内容。").build(),
        "config.json", FileInfo.builder()
            .fileName("config.json").fileSize(2048).fileType("application/json")
            .content("{\"name\":\"demo\",\"version\":\"1.0\"}").build()
    );

    public WeatherInfo getWeather(String city) {
        return weatherData.getOrDefault(city,
            WeatherInfo.builder()
                .city(city).temperature(20.0).condition("未知")
                .humidity(50.0).windSpeed(0.0).build());
    }

    public List<NewsArticle> getLatestNews(int count) {
        return newsData.stream().limit(count).toList();
    }

    public FileInfo getFile(String fileName) {
        return fileData.get(fileName);
    }

    public double calculate(String expression) {
        // 简单模拟计算器：支持加减乘除
        try {
            String sanitized = expression.replaceAll("[^0-9+\\-*/.()\\s]", "");
            if (sanitized.isEmpty()) return 0;
            // 使用 ScriptEngine 进行安全计算
            javax.script.ScriptEngine engine = new javax.script.ScriptEngineManager().getEngineByName("js");
            if (engine != null) {
                Object result = engine.eval(sanitized);
                return Double.parseDouble(result.toString());
            }
        } catch (Exception e) {
            return Double.NaN;
        }
        return Double.NaN;
    }
}
