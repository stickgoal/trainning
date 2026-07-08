package com.example.agentic.humanintheloop.controller;

import com.example.agentic.humanintheloop.service.HumanInTheLoopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/humanintheloop")
@RequiredArgsConstructor
public class HumanInTheLoopController {

    private final HumanInTheLoopService hitlService;

    @PostMapping("/refund")
    public Map<String, Object> submitRefund(
            @RequestParam String orderId,
            @RequestParam String reason,
            @RequestParam double amount) {
        log.info("HITL refund request: orderId={}, reason={}, amount={}", orderId, reason, amount);
        return hitlService.submitRefund(orderId, reason, amount);
    }

    @PostMapping("/approve")
    public Map<String, Object> approve(
            @RequestParam String requestId,
            @RequestParam String decision,
            @RequestParam(required = false) String comment) {
        log.info("HITL approve request: requestId={}, decision={}, comment={}", requestId, decision, comment);
        return hitlService.approve(requestId, decision, comment);
    }
}
