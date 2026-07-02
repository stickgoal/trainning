package com.example.agentic.parallel.controller;

import com.example.agentic.parallel.service.ParallelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Parallel 工作流 REST 入口。
 * POST /api/parallel/check?orderId=ORD-001&reason=商品质量问题
 */
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

        log.info("Parallel workflow completed for orderId={}", orderId);
        return Map.of(
            "orderId", orderId,
            "reason", reason,
            "workflowResult", result
        );
    }
}
