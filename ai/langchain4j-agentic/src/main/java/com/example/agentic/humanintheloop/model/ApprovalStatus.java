package com.example.agentic.humanintheloop.model;

/**
 * HumanInTheLoop 审批状态。
 *
 * <pre>
 *   submitRefund ──► PENDING ──(approve)──► COMPLETED   (审批通过并执行)
 *                          │                   │
 *                          │                   └──► REJECTED   (审批驳回)
 *                          │
 *                          └──(recover，模拟重启后)──► RECOVERED   (中断后恢复完成)
 *   (任意阶段异常) ─────────────────────────────────► ERROR
 * </pre>
 */
public enum ApprovalStatus {

    PENDING("PENDING", "等待人工审批"),
    APPROVED("APPROVED", "已审批"),
    COMPLETED("COMPLETED", "已完成"),
    REJECTED("REJECTED", "已驳回"),
    RECOVERED("RECOVERED", "重启恢复后完成"),
    NOT_FOUND("NOT_FOUND", "未找到"),
    ERROR("ERROR", "处理失败");

    private final String code;
    private final String label;

    ApprovalStatus(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String code() {
        return code;
    }

    public String label() {
        return label;
    }

    public static ApprovalStatus from(String code) {
        if (code == null) {
            return null;
        }
        for (ApprovalStatus s : values()) {
            if (s.code.equals(code)) {
                return s;
            }
        }
        return null;
    }

    /** 是否为终态。 */
    public boolean isTerminal() {
        return this == COMPLETED || this == REJECTED || this == RECOVERED || this == ERROR;
    }
}
