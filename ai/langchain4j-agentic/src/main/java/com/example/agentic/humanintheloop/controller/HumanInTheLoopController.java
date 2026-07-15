package com.example.agentic.humanintheloop.controller;

import com.example.agentic.humanintheloop.model.ApprovalResponse;
import com.example.agentic.humanintheloop.service.ApprovalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * HumanInTheLoop 工作流 REST 入口（LangChain4j 官方 @HumanInTheLoop 注解实现版）。
 *
 * <h3>交互流程</h3>
 * <pre>
 * 1) 提交退款（运行至审批点暂停，序列化 PendingResponse 落库）:
 *    POST /api/humanintheloop/refund?orderId=ORD-003&reason=不喜欢了&amount=918
 *    → 200，status=PENDING，返回 responseId 与序列化后的 PendingResponse JSON
 *
 * 2) 人工审批（完成 PendingResponse，恢复工作流并阻塞等待结果）:
 *    POST /api/humanintheloop/approve?businessId=ORD-003&decision=APPROVED
 *    → 200，status=COMPLETED，含 executionResult
 *
 * 3) 查询状态:
 *    GET  /api/humanintheloop/status?businessId=ORD-003
 * </pre>
 *
 * 若进程在审批等待期间重启，内存会话丢失，请改用 {@code RecoveryController} 的 /recover 恢复。
 */
@Slf4j
@RestController
@RequestMapping("/api/humanintheloop")
@RequiredArgsConstructor
public class HumanInTheLoopController {

    private final ApprovalService approvalService;

    @PostMapping("/refund")
    public ApprovalResponse submitRefund(@RequestParam String orderId,
                                         @RequestParam String reason,
                                         @RequestParam(defaultValue = "0") double amount) {
        log.info("refund request: orderId={}, reason={}, amount={}", orderId, reason, amount);
        return approvalService.submitRefund(orderId, reason, amount);
    }

    @PostMapping("/approve")
    public ResponseEntity<ApprovalResponse> approve(@RequestParam String businessId,
                                                     @RequestParam String decision,
                                                     @RequestParam(required = false) String comment) {
        log.info("approval: businessId={}, decision={}, comment={}", businessId, decision, comment);
        ApprovalResponse resp = approvalService.approve(businessId, decision, comment);
        if ("NOT_FOUND".equals(resp.getStatus())) {
            return ResponseEntity.status(404).body(resp);
        }
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/status")
    public ResponseEntity<ApprovalResponse> status(@RequestParam String businessId) {
        ApprovalResponse resp = approvalService.getStatus(businessId);
        if ("NOT_FOUND".equals(resp.getStatus())) {
            return ResponseEntity.status(404).body(resp);
        }
        return ResponseEntity.ok(resp);
    }
}
