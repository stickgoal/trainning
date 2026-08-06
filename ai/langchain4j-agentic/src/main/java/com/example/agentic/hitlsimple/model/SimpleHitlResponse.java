package com.example.agentic.hitlsimple.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一返回体。字段尽量扁平，方便在页面上直接展示与讲解。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleHitlResponse {

    private String requestId;
    private String status;
    private String statusLabel;
    private String level;
    private String preCheck;
    private String decision;
    private String comment;
    private String result;
    private String message;

    public static SimpleHitlResponse pending(PendingRequest pr, String message) {
        return SimpleHitlResponse.builder()
                .requestId(pr.getRequestId())
                .status(pr.getStatus())
                .statusLabel("等待人工审批")
                .level(pr.getLevel())
                .preCheck(pr.getPreCheck())
                .message(message)
                .build();
    }

    public static SimpleHitlResponse completed(PendingRequest pr) {
        String label = "APPROVED".equals(pr.getStatus()) ? "已通过" : "已驳回";
        return SimpleHitlResponse.builder()
                .requestId(pr.getRequestId())
                .status(pr.getStatus())
                .statusLabel(label)
                .level(pr.getLevel())
                .preCheck(pr.getPreCheck())
                .decision(pr.getDecision())
                .comment(pr.getComment())
                .result(pr.getResult())
                .message("人工审批已完成，工作流已恢复执行")
                .build();
    }

    public static SimpleHitlResponse notFound(String requestId) {
        return SimpleHitlResponse.builder()
                .requestId(requestId)
                .status("NOT_FOUND")
                .statusLabel("未找到")
                .message("该 requestId 不存在或已过期（内存态在重启后会清空）")
                .build();
    }

    public static SimpleHitlResponse alreadyDone(PendingRequest pr) {
        return SimpleHitlResponse.builder()
                .requestId(pr.getRequestId())
                .status(pr.getStatus())
                .statusLabel("已处理")
                .preCheck(pr.getPreCheck())
                .decision(pr.getDecision())
                .result(pr.getResult())
                .message("该请求已被审批（幂等保护，未重复执行）")
                .build();
    }
}
