package com.example.agentic.loop.controller;

import com.example.agentic.loop.service.LoopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Loop 工作流 REST 入口。
 * POST /api/loop/reply?orderId=ORD-001&reason=商品质量问题
 */
@Slf4j
@RestController
@RequestMapping("/api/loop")
@RequiredArgsConstructor
public class LoopController {

    private final LoopService loopService;

    @PostMapping("/reply")
    public Map<String, Object> generateReply(
            @RequestParam String orderId,
            @RequestParam String reason) {
        log.info("Loop reply request: orderId={}, reason={}", orderId, reason);

        String result = loopService.generateReply(orderId, reason);

        log.info("Loop workflow completed for orderId={}", orderId);
        return Map.of(
            "orderId", orderId,
            "reason", reason,
            "workflowResult", result
        );
    }
}
