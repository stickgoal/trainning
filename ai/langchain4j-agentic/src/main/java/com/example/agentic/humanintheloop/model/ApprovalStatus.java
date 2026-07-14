package com.example.agentic.humanintheloop.model;

/**
 * 人工审批(HumanInTheLoop)请求状态机。
 *
 * <pre>
 *   submitRefund ──► PENDING_PRECHECK ──► AWAITING_APPROVAL
 *                                                 │ approve(原子翻转)
 *                                                 ▼
 *                                            EXECUTING ──► EXECUTED   (审批通过并执行退款)
 *                                                 │
 *                                                 └────────► REJECTED  (审批驳回)
 *   (任意阶段异常) ─────────────────────────────────► FAILED
 * </pre>
 *
 * 状态全部持久化于 {@code approval_request} 表，进程重启后可恢复，不再依赖内存 Map。
 */
public enum ApprovalStatus {

    PENDING_PRECHECK("PENDING_PRECHECK", "前置检查中"),
    AWAITING_APPROVAL("AWAITING_APPROVAL", "等待人工审批"),
    EXECUTING("EXECUTING", "审批通过，执行中"),
    EXECUTED("EXECUTED", "已执行(退款)"),
    REJECTED("REJECTED", "已驳回"),
    FAILED("FAILED", "处理失败");

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

    /** 是否为终态（无需再轮询）。 */
    public boolean isTerminal() {
        return this == EXECUTED || this == REJECTED || this == FAILED;
    }
}
