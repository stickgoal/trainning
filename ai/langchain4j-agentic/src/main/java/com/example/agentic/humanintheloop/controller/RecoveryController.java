package com.example.agentic.humanintheloop.controller;

import com.example.agentic.humanintheloop.entity.HitlPendingEntity;
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

import java.util.List;

/**
 * 重启恢复控制器：模拟应用重启后，按业务ID恢复并完成中断的审批流程。
 *
 * <h3>典型演示路径</h3>
 * <pre>
 * 1) 提交退款（工作流暂停，PendingResponse 已序列化落库）:
 *    POST /api/humanintheloop/refund?orderId=ORD-003&reason=不喜欢了&amount=918
 *
 * 2) 模拟进程重启（清除内存会话，DB 记录保留）:
 *    POST /api/humanintheloop/simulate-restart?businessId=ORD-003
 *
 * 3) 查看待恢复记录:
 *    GET  /api/humanintheloop/recover/pending
 *
 * 4) 恢复并完成中断的审批（反序列化 PendingResponse → 重跑 → 注入结论）:
 *    POST /api/humanintheloop/recover?businessId=ORD-003&decision=APPROVED
 *    → 200，status=RECOVERED，含 executionResult
 * </pre>
 *
 * <p>第 4 步会在日志中打印 PendingResponse 的反序列化过程：
 * 从 DB 中的 JSON 还原出 responseId，并重建一个新的 CompletableFuture。</p>
 */
@Slf4j
@RestController
@RequestMapping("/api/humanintheloop")
@RequiredArgsConstructor
public class RecoveryController {

    private final ApprovalService approvalService;

    /** 模拟进程重启：清除某业务的内存运行态，但保留 DB 中的序列化记录。 */
    @PostMapping("/simulate-restart")
    public ApprovalResponse simulateRestart(@RequestParam String businessId) {
        log.info("simulate-restart: businessId={}", businessId);
        return approvalService.simulateRestart(businessId);
    }

    /** 列出所有中断/等待中的审批记录（可用于重启后排查）。 */
    @GetMapping("/recover/pending")
    public List<HitlPendingEntity> pending() {
        return approvalService.listPending();
    }

    /** 恢复并完成中断的审批：反序列化 PendingResponse，重跑工作流并注入审批结论。 */
    @PostMapping("/recover")
    public ResponseEntity<ApprovalResponse> recover(@RequestParam String businessId,
                                                     @RequestParam String decision,
                                                     @RequestParam(required = false) String comment) {
        log.info("recover: businessId={}, decision={}, comment={}", businessId, decision, comment);
        ApprovalResponse resp = approvalService.recover(businessId, decision, comment);
        if ("NOT_FOUND".equals(resp.getStatus())) {
            return ResponseEntity.status(404).body(resp);
        }
        return ResponseEntity.ok(resp);
    }
}
