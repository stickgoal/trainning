package com.example.agentic.supervisor.controller;

import com.example.agentic.supervisor.service.SupervisorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Supervisor 工作流 REST 入口。
 * POST /api/supervisor/handle?orderId=ORD-001&complaint=收到商品有质量问题，非常生气，要求退款并赔偿
 */
@Slf4j
@RestController
@RequestMapping("/api/supervisor")
@RequiredArgsConstructor
public class SupervisorController {

    private final SupervisorService supervisorService;

    @PostMapping("/handle")
    public Map<String, Object> handleComplaint(
            @RequestParam String orderId,
            @RequestParam String complaint) {
        log.info("Supervisor handle request: orderId={}, complaint={}", orderId, complaint);

        String result = supervisorService.handleComplaint(orderId, complaint);

        log.info("Supervisor workflow completed for orderId={}", orderId);
        return Map.of(
            "orderId", orderId,
            "complaint", complaint,
            "workflowResult", result
        );
    }
}
