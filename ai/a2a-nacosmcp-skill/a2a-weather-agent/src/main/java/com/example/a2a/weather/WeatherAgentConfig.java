package com.example.a2a.weather;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.alibaba.cloud.ai.graph.agent.ReactAgent;

import java.util.Map;

/**
 * 天气智能体配置
 * 功能点：演示不同类型工具（天气查询、空气质量）的 A2A 远程调用
 */
@Configuration
public class WeatherAgentConfig {

    @Bean
    public ReactAgent weatherAgent(ChatModel chatModel, WeatherTools weatherTools) {
        return ReactAgent.builder()
                .name("weather_agent")
                .description("天气查询智能体，提供城市天气查询和空气质量查询服务")
                .model(chatModel)
                .instruction("""
                        你是一个专业的天气助手。
                        职责：
                        1. 查询指定城市的天气信息
                        2. 查询指定城市的空气质量
                        3. 根据天气情况给出出行建议
                        回答要求：
                        - 信息准确，格式清晰
                        - 主动给出穿衣、出行等建议
                        """)
                .tools(weatherTools)
                .build();
    }
}

/**
 * 天气工具类
 * 功能点：模拟天气数据查询，实际场景可对接真实天气 API
 */
@Component
class WeatherTools {

    private static final Map<String, String> WEATHER_DB = Map.of(
            "北京", "晴，25°C，湿度45%，东南风2级",
            "上海", "多云，28°C，湿度65%，东风3级",
            "广州", "雷阵雨，30°C，湿度80%，南风4级",
            "深圳", "阴转晴，29°C，湿度70%，西南风3级",
            "杭州", "小雨，24°C，湿度75%，北风2级",
            "成都", "阴，22°C，湿度60%，无持续风向",
            "重庆", "多云，32°C，湿度55%，东南风2级",
            "西安", "晴，27°C，湿度40%，东北风3级",
            "武汉", "多云转晴，26°C，湿度50%，东风2级",
            "南京", "阴，25°C，湿度55%，东风3级"
    );

    private static final Map<String, String> AQI_DB = Map.of(
            "北京", "AQI: 52，良，PM2.5: 22μg/m³",
            "上海", "AQI: 45，优，PM2.5: 18μg/m³",
            "广州", "AQI: 68，良，PM2.5: 35μg/m³",
            "深圳", "AQI: 38，优，PM2.5: 15μg/m³",
            "杭州", "AQI: 55，良，PM2.5: 25μg/m³",
            "成都", "AQI: 72，良，PM2.5: 40μg/m³",
            "重庆", "AQI: 60，良，PM2.5: 30μg/m³",
            "西安", "AQI: 85，良，PM2.5: 50μg/m³",
            "武汉", "AQI: 48，优，PM2.5: 20μg/m³",
            "南京", "AQI: 58，良，PM2.5: 28μg/m³"
    );

    @Tool(description = "查询指定城市的天气情况")
    public String getWeather(
            @ToolParam(description = "城市名称，如北京、上海") String city) {
        return WEATHER_DB.getOrDefault(city,
                "抱歉，暂未收录「" + city + "」的天气数据。已收录城市：北京、上海、广州、深圳、杭州、成都、重庆、西安、武汉、南京");
    }

    @Tool(description = "查询指定城市的空气质量指数(AQI)")
    public String getAirQuality(
            @ToolParam(description = "城市名称，如北京、上海") String city) {
        return AQI_DB.getOrDefault(city,
                "抱歉，暂未收录「" + city + "」的空气质量数据。");
    }
}