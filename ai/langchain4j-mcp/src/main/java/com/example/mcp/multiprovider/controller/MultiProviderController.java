package com.example.mcp.multiprovider.controller;

import com.example.mcp.multiprovider.MultiProviderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 多 MCP Provider REST 入口。
 * POST /api/multiprovider/ask?question=xxx
 */
@Slf4j
@RestController
@RequestMapping("/api/multiprovider")
@RequiredArgsConstructor
public class MultiProviderController {

    private final MultiProviderService multiProviderService;

    @PostMapping("/ask")
    public Map<String, Object> ask(@RequestParam String question) {
        log.info("MultiProvider ask: question={}", question);

        String answer = multiProviderService.ask(question);

        return Map.of(
            "question", question,
            "answer", answer,
            "transport", "multi-provider"
        );
    }
}
