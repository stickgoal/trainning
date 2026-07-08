package com.example.agentic.supervisor.controller;

import com.example.agentic.supervisor.service.SupervisorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
        return Map.of("orderId", orderId, "complaint", complaint, "workflowResult", result);
    }
}
