package com.example.mcp.stdio.controller;

import com.example.mcp.stdio.StdioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * stdio 传输 MCP REST 入口。
 * POST /api/stdio/ask?question=北京天气怎么样
 */
@Slf4j
@RestController
@RequestMapping("/api/stdio")
@RequiredArgsConstructor
public class StdioController {

    private final StdioService stdioService;

    @PostMapping("/ask")
    public Map<String, Object> ask(@RequestParam String question) {
        log.info("Stdio ask: question={}", question);

        String answer = stdioService.ask(question);

        return Map.of(
            "question", question,
            "answer", answer,
            "transport", "stdio"
        );
    }
}
