package com.example.agentic.humanintheloop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 人工审批(HumanInTheLoop)请求实体，对应数据库 {@code approval_request} 表。
 *
 * 该表是「暂停 → 等待人工 → 恢复执行」唯一的状态真相来源(source of truth)，
 * 取代原实现中 {@code runningWorkflows} 这种内存 {@code Map<String, Future>} 的易失状态。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("approval_request")
public class ApprovalRequestEntity {

    /** 审批请求ID，如 REQ-xxxxxxxx（同时作为业务幂等键） */
    @TableId(type = IdType.INPUT)
    private String requestId;

    /** 关联订单ID */
    private String orderId;

    /** 退款原因 */
    private String reason;

    /** 申请退款金额 */
    private Double amount;

    /** 前置检查 Agent 产出材料（供人工审批参考） */
    private String precheckResult;

    /** 状态机编码，见 {@link com.example.agentic.humanintheloop.model.ApprovalStatus} */
    private String status;

    /** 审批结论：APPROVED / REJECTED */
    private String decision;

    /** 审批备注 */
    private String decisionComment;

    /** 审批人 */
    private String approver;

    /** 执行 Agent 产出结果 */
    private String executionResult;

    /** 失败原因（status=FAILED 时） */
    private String errorMessage;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
}
