package com.example.agentic.humanintheloop.model;

import com.example.agentic.humanintheloop.entity.ApprovalRequestEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 人工审批请求的对外响应模型（视图对象）。
 * 相比原实现直接返回 {@code Map<String, Object>}，这里用强类型 DTO，便于契约稳定与前端消费。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalResponse {

    private String requestId;
    private String orderId;
    private String reason;
    private Double amount;

    private String status;
    private String statusLabel;

    private String precheckResult;
    private String decision;
    private String decisionComment;
    private String approver;
    private String executionResult;
    private String errorMessage;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;

    /** 面向调用方的提示（如轮询地址、不可审批原因等） */
    private String message;

    public static ApprovalResponse from(ApprovalRequestEntity e) {
        if (e == null) {
            return null;
        }
        ApprovalStatus status = ApprovalStatus.from(e.getStatus());
        return ApprovalResponse.builder()
                .requestId(e.getRequestId())
                .orderId(e.getOrderId())
                .reason(e.getReason())
                .amount(e.getAmount())
                .status(e.getStatus())
                .statusLabel(status != null ? status.label() : e.getStatus())
                .precheckResult(e.getPrecheckResult())
                .decision(e.getDecision())
                .decisionComment(e.getDecisionComment())
                .approver(e.getApprover())
                .executionResult(e.getExecutionResult())
                .errorMessage(e.getErrorMessage())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .completedAt(e.getCompletedAt())
                .build();
    }
}
