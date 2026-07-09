package com.example.mcp.http.controller;

import com.example.mcp.http.HttpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * HTTP/SSE 传输 MCP REST 入口。
 * POST /api/http/ask?question=xxx
 */
@Slf4j
@RestController
@RequestMapping("/api/http")
@RequiredArgsConstructor
public class HttpController {

    private final HttpService httpService;

    @PostMapping("/ask")
    public Map<String, Object> ask(@RequestParam String question) {
        log.info("Http ask: question={}", question);

        String answer = httpService.ask(question);

        return Map.of(
            "question", question,
            "answer", answer,
            "transport", "http-sse"
        );
    }
}
