package com.example.agentic.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String userId;
    private String name;
    private VipLevel vipLevel;
    private int orderCount;
    private int refundCount;
}
