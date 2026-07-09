package com.example.mcp.aitools.controller;

import com.example.mcp.aitools.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 天气助手 REST 入口。
 * POST /api/weather/ask?question=xxx
 */
@Slf4j
@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    @PostMapping("/ask")
    public Map<String, Object> ask(@RequestParam String question) {
        log.info("Weather ask: question={}", question);

        String answer = weatherService.ask(question);

        return Map.of(
            "question", question,
            "answer", answer,
            "transport", "stdio",
            "assistant", "weather-assistant"
        );
    }
}
