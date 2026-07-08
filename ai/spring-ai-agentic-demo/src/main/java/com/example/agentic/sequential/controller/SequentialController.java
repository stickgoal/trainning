package com.example.agentic.sequential.controller;

import com.example.agentic.sequential.service.SequentialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Sequential 工作流 REST 入口。
 * POST /api/sequential/refund?orderId=ORD-001&reason=商品质量问题
 */
@Slf4j
@RestController
@RequestMapping("/api/sequential")
@RequiredArgsConstructor
public class SequentialController {

    private final SequentialService sequentialService;

    @PostMapping("/refund")
    public Map<String, Object> processRefund(
            @RequestParam String orderId,
            @RequestParam String reason) {
        log.info("Processing refund request: orderId={}, reason={}", orderId, reason);

        String result = sequentialService.processRefund(orderId, reason);

        log.info("Sequential workflow completed for orderId={}", orderId);
        return Map.of(
            "orderId", orderId,
            "reason", reason,
            "workflowResult", result
        );
    }
}
