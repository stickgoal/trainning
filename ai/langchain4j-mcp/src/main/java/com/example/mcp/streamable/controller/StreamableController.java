package com.example.mcp.streamable.controller;

import com.example.mcp.streamable.StreamableService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Streamable HTTP 传输 MCP REST 入口。
 * POST /api/streamable/ask?question=xxx
 */
@Slf4j
@RestController
@RequestMapping("/api/streamable")
@RequiredArgsConstructor
public class StreamableController {

    private final StreamableService streamableService;

    @PostMapping("/ask")
    public Map<String, Object> ask(@RequestParam String question) {
        log.info("Streamable ask: question={}", question);

        String answer = streamableService.ask(question);

        return Map.of(
            "question", question,
            "answer", answer,
            "transport", "streamable-http"
        );
    }
}
