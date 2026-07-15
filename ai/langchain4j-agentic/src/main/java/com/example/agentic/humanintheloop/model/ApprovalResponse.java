package com.example.agentic.humanintheloop.model;

import com.example.agentic.humanintheloop.entity.HitlPendingEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * HumanInTheLoop 接口统一返回体。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalResponse {

    /** 业务ID（orderId） */
    private String businessId;

    /** 状态编码，见 {@link ApprovalStatus} */
    private String status;

    /** 状态中文说明 */
    private String statusLabel;

    /** PendingResponse 的 responseId（如 approval:ORD-003） */
    private String responseId;

    /** 前置检查材料 */
    private String precheckResult;

    /** 审批结论 */
    private String decision;

    /** 最终执行结果 */
    private String result;

    /** PendingResponse 序列化后的 JSON（便于直观看到序列化内容） */
    private String serializedPending;

    /** 提示信息 */
    private String message;

    public static ApprovalResponse from(HitlPendingEntity e, String precheckResult, String message) {
        ApprovalStatus s = ApprovalStatus.from(e == null ? null : e.getStatus());
        return ApprovalResponse.builder()
                .businessId(e == null ? null : e.getBusinessId())
                .status(e == null ? null : e.getStatus())
                .statusLabel(s == null ? null : s.label())
                .responseId(e == null ? null : e.getResponseId())
                .precheckResult(precheckResult)
                .decision(e == null ? null : e.getDecision())
                .result(e == null ? null : e.getResult())
                .serializedPending(e == null ? null : e.getSerializedPending())
                .message(message)
                .build();
    }

    public static ApprovalResponse notFound(String businessId, String message) {
        return ApprovalResponse.builder()
                .businessId(businessId)
                .status(ApprovalStatus.NOT_FOUND.code())
                .statusLabel(ApprovalStatus.NOT_FOUND.label())
                .message(message)
                .build();
    }
}
