package com.example.agentic.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Health / info endpoint for the agentic demo.
 */
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HealthController {

    @GetMapping("/health")
    public String health() {
        return "langchain4j-agentic-demo is running";
    }
}
