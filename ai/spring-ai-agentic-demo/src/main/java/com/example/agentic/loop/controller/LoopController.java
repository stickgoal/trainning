package com.example.agentic.loop.controller;

import com.example.agentic.loop.service.LoopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
        return Map.of("orderId", orderId, "reason", reason, "workflowResult", result);
    }
}
