package com.example.agentic.humanintheloop.controller;

import com.example.agentic.humanintheloop.service.HumanInTheLoopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * HumanInTheLoop 工作流 REST 入口。
 * 
 * 两步交互演示「暂停 → 等待人工 → 恢复」：
 * <pre>
 * 1) 提交退款（运行到审批环节暂停）:
 *    POST /api/humanintheloop/refund?orderId=ORD-003&reason=不喜欢了&amount=918
 *    → 返回 requestId 与前置检查材料
 *
 * 2) 人工审批（恢复执行）:
 *    POST /api/humanintheloop/approve?requestId=REQ-xxxx&decision=APPROVED
 *    → 返回最终执行结果
 * </pre>
 */
@Slf4j
@RestController
@RequestMapping("/api/humanintheloop")
@RequiredArgsConstructor
public class HumanInTheLoopController {

    private final HumanInTheLoopService humanInTheLoopService;

    /**
     * 提交需人工审批的大额退款申请，工作流运行至审批环节后暂停。
     */
    @PostMapping("/refund")
    public Map<String, Object> submitRefund(
            @RequestParam String orderId,
            @RequestParam String reason,
            @RequestParam(defaultValue = "0") double amount) {
        log.info("HumanInTheLoop refund request: orderId={}, reason={}, amount={}", orderId, reason, amount);
        return humanInTheLoopService.submitRefund(orderId, reason, amount);
    }

    /**
     * 提交人工审批结论，恢复工作流执行。
     */
    @PostMapping("/approve")
    public Map<String, Object> approve(
            @RequestParam String requestId,
            @RequestParam String decision,
            @RequestParam(required = false) String comment) {
        log.info("HumanInTheLoop approval: requestId={}, decision={}, comment={}", requestId, decision, comment);
        return humanInTheLoopService.approve(requestId, decision, comment);
    }
}
