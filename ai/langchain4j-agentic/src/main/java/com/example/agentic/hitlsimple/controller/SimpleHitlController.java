package com.example.agentic.hitlsimple.controller;

import com.example.agentic.hitlsimple.model.PendingRequest;
import com.example.agentic.hitlsimple.model.SimpleHitlResponse;
import com.example.agentic.hitlsimple.service.SimpleHitlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 极简 HumanInTheLoop 的 REST 入口（仅用于讲解原理，无框架依赖）。
 *
 * <h3>三个端点正好对应 HITL 的三个动作</h3>
 * <pre>
 *  POST /api/hitls/submit   提交，跑到审批点挂起，返回 requestId（状态 PENDING）
 *  POST /api/hitls/approve  凭 requestId 人工审批，恢复执行，返回最终结果
 *  GET  /api/hitls/status   查状态    GET /api/hitls/pending  列出待审批
 * </pre>
 */
@Slf4j
@RestController
@RequestMapping("/api/hitls")
@RequiredArgsConstructor
public class SimpleHitlController {

    private final SimpleHitlService service;

    @PostMapping("/submit")
    public SimpleHitlResponse submit(@RequestParam String orderId,
                                     @RequestParam String reason,
                                     @RequestParam(defaultValue = "0") double amount) {
        return service.submit(orderId, reason, amount);
    }

    @PostMapping("/approve")
    public ResponseEntity<SimpleHitlResponse> approve(@RequestParam String requestId,
                                                      @RequestParam String decision,
                                                      @RequestParam(required = false) String comment) {
        SimpleHitlResponse resp = service.approve(requestId, decision, comment);
        if ("NOT_FOUND".equals(resp.getStatus())) {
            return ResponseEntity.status(404).body(resp);
        }
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/status")
    public ResponseEntity<SimpleHitlResponse> status(@RequestParam String requestId) {
        SimpleHitlResponse resp = service.status(requestId);
        if ("NOT_FOUND".equals(resp.getStatus())) {
            return ResponseEntity.status(404).body(resp);
        }
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/pending")
    public List<PendingRequest> pending() {
        return service.listPending();
    }
}
