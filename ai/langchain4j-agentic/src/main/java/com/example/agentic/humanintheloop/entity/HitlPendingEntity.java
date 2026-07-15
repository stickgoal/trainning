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
 * HumanInTheLoop 待审批持久化记录，对应数据库 {@code hitl_pending} 表。
 *
 * <p>本表的核心作用：保存被 {@code @HumanInTheLoop} 暂停的 {@code PendingResponse}
 * <b>序列化后的 JSON</b>（{@link #serializedPending}），以及业务上下文。
 * 进程重启后，内存中的 AgenticScope 与阻塞线程都会丢失，但本表的记录仍在，
 * {@code RecoveryController} 可按 {@link #businessId} 反序列化恢复并完成中断的审批。</p>
 *
 * <p>注意：{@code PendingResponse} 经 Jackson 序列化后只保留 {@code responseId}
 * （{@code CompletableFuture} 被标为 {@code @JsonIgnore}），反序列化时会重建一个新的
 * 未完成 future —— 这正是「跨进程恢复」的语义基础。</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("hitl_pending")
public class HitlPendingEntity {

    /** 业务ID（取 orderId），作为主键与恢复依据 */
    @TableId(type = IdType.INPUT)
    private String businessId;

    /** 关联订单ID */
    private String orderId;

    /** 退款原因 */
    private String reason;

    /** 申请退款金额 */
    private Double amount;

    /** PendingResponse 的 responseId，如 "approval:ORD-003" */
    private String responseId;

    /** PendingResponse 序列化后的 JSON，如 {"responseId":"approval:ORD-003"} */
    private String serializedPending;

    /** 前置检查材料（供人工审批参考） */
    private String precheckResult;

    /** 状态：PENDING / APPROVED / COMPLETED / REJECTED / RECOVERED / ERROR */
    private String status;

    /** 审批结论 */
    private String decision;

    /** 最终执行结果 */
    private String result;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
