package com.example.agentic.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private String orderId;
    private String userId;
    private List<OrderItem> items;
    private OrderStatus status;
    private double totalPrice;
}
