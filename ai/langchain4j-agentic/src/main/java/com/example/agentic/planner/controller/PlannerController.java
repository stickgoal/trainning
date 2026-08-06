package com.example.agentic.planner.controller;

import com.example.agentic.planner.service.PlannerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Planner 工作流 REST 入口。
 * 
 * <pre>
 * POST /api/planner/plan
 * Content-Type: application/json
 * 
 * {
 *   "tickets": [
 *     {"orderId": "ORD-003", "issue": "收到商品破损，要求全额退款并赔偿"},
 *     {"orderId": "ORD-001", "issue": "蓝牙耳机连接不稳定，要求换货"},
 *     {"orderId": "ORD-002", "issue": "不喜欢键盘手感，申请退款"},
 *     {"orderId": "ORD-004", "issue": "咨询发货时间"}
 *   ]
 * }
 * </pre>
 */
@Slf4j
@RestController
@RequestMapping("/api/planner")
@RequiredArgsConstructor
public class PlannerController {

    private final PlannerService plannerService;

    @PostMapping("/plan")
    public Map<String, Object> handleBatch(@RequestBody BatchTicketsRequest request) {
        log.info("Planner batch request: {} tickets", request.tickets().size());

        // 将批量工单转为 JSON 字符串
        StringBuilder jsonBuilder = new StringBuilder("{\"tickets\":[");
        for (int i = 0; i < request.tickets().size(); i++) {
            Ticket t = request.tickets().get(i);
            if (i > 0) jsonBuilder.append(",");
            jsonBuilder.append("{\"orderId\":\"").append(t.orderId())
                    .append("\",\"issue\":\"").append(t.issue()).append("\"}");
        }
        jsonBuilder.append("]}");
        String ticketsJson = jsonBuilder.toString();

        String result = plannerService.handleBatch(ticketsJson);

        log.info("Planner workflow completed, {} tickets processed", request.tickets().size());
        return Map.of(
            "totalTickets", request.tickets().size(),
            "workflowResult", result
        );
    }

    /**
     * 批量工单请求体
     */
    public record BatchTicketsRequest(List<Ticket> tickets) {}

    /**
     * 单个工单
     */
    public record Ticket(String orderId, String issue) {}
}
