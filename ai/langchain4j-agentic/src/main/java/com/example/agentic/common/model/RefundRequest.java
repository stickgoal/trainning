package com.example.agentic.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundRequest {
    private String requestId;
    private String orderId;
    private String userId;
    private String reason;
    private double amount;
    private RefundStatus status;
}
