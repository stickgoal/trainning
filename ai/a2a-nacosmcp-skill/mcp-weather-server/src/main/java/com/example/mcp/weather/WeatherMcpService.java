package com.example.mcp.weather;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * MCP 天气服务
 * 功能点：通过 @Tool 注解定义 MCP 工具，自动注册到 Nacos MCP Registry，
 * 客户端可通过 Nacos 发现并调用这些工具
 */
@Service
public class WeatherMcpService {

    private static final Map<String, String> WEATHER_DB = Map.of(
            "北京", "晴，25°C，湿度45%，东南风2级，紫外线中等",
            "上海", "多云，28°C，湿度65%，东风3级，紫外线弱",
            "广州", "雷阵雨，30°C，湿度80%，南风4级，紫外线弱",
            "深圳", "阴转晴，29°C，湿度70%，西南风3级，紫外线强",
            "杭州", "小雨，24°C，湿度75%，北风2级，紫外线弱",
            "成都", "阴，22°C，湿度60%，无持续风向，紫外线弱",
            "重庆", "多云，32°C，湿度55%，东南风2级，紫外线中等",
            "西安", "晴，27°C，湿度40%，东北风3级，紫外线强",
            "武汉", "多云转晴，26°C，湿度50%，东风2级，紫外线中等",
            "南京", "阴，25°C，湿度55%，东风3级，紫外线弱"
    );

    private static final Map<String, String> AQI_DB = Map.of(
            "北京", "AQI: 52，级别：良，首要污染物：PM10",
            "上海", "AQI: 45，级别：优",
            "广州", "AQI: 68，级别：良，首要污染物：O₃",
            "深圳", "AQI: 38，级别：优",
            "杭州", "AQI: 55，级别：良，首要污染物：PM2.5",
            "成都", "AQI: 72，级别：良，首要污染物：PM2.5",
            "重庆", "AQI: 60，级别：良，首要污染物：PM2.5",
            "西安", "AQI: 85，级别：良，首要污染物：PM10",
            "武汉", "AQI: 48，级别：优",
            "南京", "AQI: 58，级别：良，首要污染物：PM2.5"
    );

    @Tool(description = "查询指定城市的实时天气信息，包括温度、湿度、风向、紫外线强度")
    public String queryWeather(
            @ToolParam(description = "城市名称，如北京、上海") String city) {
        return WEATHER_DB.getOrDefault(city,
                "未收录「" + city + "」的天气数据。支持城市：北京、上海、广州、深圳、杭州、成都、重庆、西安、武汉、南京");
    }

    @Tool(description = "查询指定城市的空气质量指数(AQI)，包括级别和首要污染物")
    public String queryAirQuality(
            @ToolParam(description = "城市名称，如北京、上海") String city) {
        return AQI_DB.getOrDefault(city,
                "未收录「" + city + "」的空气质量数据。");
    }

    @Tool(description = "根据天气情况给出出行建议")
    public String getTravelAdvice(
            @ToolParam(description = "城市名称") String city) {
        String weather = WEATHER_DB.get(city);
        if (weather == null) {
            return "无法为「" + city + "」生成出行建议，城市数据未收录。";
        }

        StringBuilder advice = new StringBuilder();
        advice.append("【").append(city).append("出行建议】\n");

        if (weather.contains("雨")) {
            advice.append("🌂 建议携带雨具\n");
        }
        if (weather.contains("晴")) {
            advice.append("🧴 注意防晒，建议涂抹防晒霜\n");
        }
        if (weather.contains("紫外线强")) {
            advice.append("🕶️ 紫外线较强，建议佩戴太阳镜\n");
        }
        String temp = weather.replaceAll(".*?(\\d+)°C.*", "$1");
        try {
            int t = Integer.parseInt(temp);
            if (t > 30) {
                advice.append("👕 天气炎热，建议穿着轻薄透气的衣物\n");
            } else if (t < 20) {
                advice.append("🧥 温度较低，建议添加外套\n");
            } else {
                advice.append("👔 温度适宜，穿着舒适即可\n");
            }
        } catch (NumberFormatException ignored) {}

        return advice.toString();
    }
}