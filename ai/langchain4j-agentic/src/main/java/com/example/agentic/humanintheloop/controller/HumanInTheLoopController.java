package com.example.agentic.humanintheloop.controller;

import com.example.agentic.humanintheloop.model.ApprovalResponse;
import com.example.agentic.humanintheloop.service.HumanInTheLoopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * HumanInTheLoop 工作流 REST 入口（生产化改造版）。
 *
 * <h3>非阻塞三步交互</h3>
 * <pre>
 * 1) 提交退款（前置检查后进入等待人工审批，立即返回）:
 *    POST /api/humanintheloop/refund?orderId=ORD-003&reason=不喜欢了&amount=918
 *    → 200，返回 requestId 与前置检查材料，status=AWAITING_APPROVAL
 *
 * 2) 人工审批（落库即返回，不阻塞）:
 *    POST /api/humanintheloop/approve?requestId=REQ-xxxx&decision=APPROVED
 *    → 202 Accepted，status=EXECUTING，执行在后台进行
 *
 * 3) 轮询结果（非阻塞获取最终执行结果）:
 *    GET  /api/humanintheloop/status?requestId=REQ-xxxx
 *    → 200，status=EXECUTED / REJECTED / FAILED，含 executionResult
 * </pre>
 *
 * 对比原实现：原 /approve 内部 {@code future.get(120s)} 阻塞 HTTP 线程直到工作流跑完；
 * 这里审批只是「受理 + 调度」，立刻返回，结果通过 /status 轮询，彻底释放 Web 线程。
 */
@Slf4j
@RestController
@RequestMapping("/api/humanintheloop")
@RequiredArgsConstructor
public class HumanInTheLoopController {

    private final HumanInTheLoopService humanInTheLoopService;

    /**
     * 提交需人工审批的大额退款申请，运行前置检查后进入等待人工审批，立即返回。
     */
    @PostMapping("/refund")
    public ApprovalResponse submitRefund(@RequestParam String orderId,
                                          @RequestParam String reason,
                                          @RequestParam(defaultValue = "0") double amount) {
        log.info("HumanInTheLoop refund request: orderId={}, reason={}, amount={}", orderId, reason, amount);
        return humanInTheLoopService.submitRefund(orderId, reason, amount);
    }

    /**
     * 提交人工审批结论。<b>非阻塞</b>：仅接管控权并调度异步执行，立即返回 202 Accepted。
     * 最终执行结果请通过 {@code GET /api/humanintheloop/status?requestId=...} 轮询。
     */
    @PostMapping("/approve")
    public ResponseEntity<ApprovalResponse> approve(@RequestParam String requestId,
                                                    @RequestParam String decision,
                                                    @RequestParam(required = false) String comment,
                                                    @RequestParam(required = false) String approver) {
        log.info("HumanInTheLoop approval: requestId={}, decision={}, comment={}, approver={}",
                requestId, decision, comment, approver);
        ApprovalResponse resp = humanInTheLoopService.approve(requestId, decision, comment, approver);
        if ("NOT_FOUND".equals(resp.getStatus())) {
            return ResponseEntity.status(404).body(resp);
        }
        // 202 Accepted：已受理，执行在后台进行
        return ResponseEntity.accepted().body(resp);
    }

    /**
     * 查询审批请求当前快照（用于轮询获取最终执行结果）。
     */
    @GetMapping("/status")
    public ResponseEntity<ApprovalResponse> status(@RequestParam String requestId) {
        ApprovalResponse resp = humanInTheLoopService.getStatus(requestId);
        if (resp == null) {
            return ResponseEntity.status(404).body(ApprovalResponse.builder()
                    .requestId(requestId)
                    .status("NOT_FOUND")
                    .message("未找到该审批请求")
                    .build());
        }
        return ResponseEntity.ok(resp);
    }
}
