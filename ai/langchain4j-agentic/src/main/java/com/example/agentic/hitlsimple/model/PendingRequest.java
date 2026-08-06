package com.example.agentic.hitlsimple.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 挂起请求（Suspended Request）—— HumanInTheLoop 的核心载体。
 *
 * <p>它代表「工作流在等待人工决策期间，被冻结保存下来的那一整段上下文」。
 * 只要这个对象还在（内存 Map / 数据库 / Redis），工作流就随时能被找回并恢复。</p>
 *
 * <p>这对应 LangChain4j 官方实现里的 {@code PendingResponse} + {@code AgenticScope}：
 * 框架帮我们隐式地维护它；这里我们用一个最直白的 Java 对象，把它「显形」出来。</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PendingRequest {

    /** 关联令牌：连接「提交」与「审批」这两条 HTTP 请求。 */
    private String requestId;

    /** 业务ID（订单号）。 */
    private String orderId;

    /** 退款原因。 */
    private String reason;

    /** 申请金额。 */
    private double amount;

    /** 风控等级（高/中/低）—— 提交阶段由确定性规则算出，供审批人参考。 */
    private String level;

    /** 预处理结论（提交阶段自动产出，相当于 LLM Agent 跑完「审批前」步骤的结果）。 */
    private String preCheck;

    /** 状态：PENDING（等人工）/ APPROVED（已通过）/ REJECTED（已驳回）。 */
    private String status;

    /** 人工决策（APPROVED / REJECTED）。 */
    private String decision;

    /** 审批备注。 */
    private String comment;

    /** 恢复后自动执行（「审批后」步骤）得到的最终结果。 */
    private String result;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
