package com.example.agentic.conditional.controller;

import com.example.agentic.conditional.service.ConditionalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/conditional")
@RequiredArgsConstructor
public class ConditionalController {

    private final ConditionalService conditionalService;

    @PostMapping("/refund")
    public Map<String, Object> processRefund(
            @RequestParam String orderId,
            @RequestParam String reason) {
        log.info("Conditional refund request: orderId={}, reason={}", orderId, reason);
        String result = conditionalService.processRefund(orderId, reason);
        return Map.of("orderId", orderId, "reason", reason, "workflowResult", result);
    }
}
