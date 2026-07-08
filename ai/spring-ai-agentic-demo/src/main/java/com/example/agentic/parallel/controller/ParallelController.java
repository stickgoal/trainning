package com.example.agentic.parallel.controller;

import com.example.agentic.parallel.service.ParallelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/parallel")
@RequiredArgsConstructor
public class ParallelController {

    private final ParallelService parallelService;

    @PostMapping("/check")
    public Map<String, Object> parallelCheck(
            @RequestParam String orderId,
            @RequestParam String reason) {
        log.info("Parallel check request: orderId={}, reason={}", orderId, reason);
        String result = parallelService.parallelCheck(orderId, reason);
        return Map.of("orderId", orderId, "reason", reason, "workflowResult", result);
    }
}
