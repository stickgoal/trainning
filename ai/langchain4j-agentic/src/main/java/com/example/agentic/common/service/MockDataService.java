package com.example.agentic.common.service;

import com.example.agentic.common.model.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 模拟数据服务，提供电商售后场景所需的测试数据。
 */
@Service
public class MockDataService {

    private final Map<String, Order> orders = Map.of(
        "ORD-001", Order.builder()
            .orderId("ORD-001").userId("USR-001")
            .items(List.of(new OrderItem("PRD-001", "蓝牙耳机", 1, 299.0)))
            .status(OrderStatus.DELIVERED).totalPrice(299.0).build(),
        "ORD-002", Order.builder()
            .orderId("ORD-002").userId("USR-002")
            .items(List.of(new OrderItem("PRD-002", "机械键盘", 1, 599.0)))
            .status(OrderStatus.SHIPPED).totalPrice(599.0).build(),
        "ORD-003", Order.builder()
            .orderId("ORD-003").userId("USR-003")
            .items(List.of(new OrderItem("PRD-003", "运动跑鞋", 2, 459.0)))
            .status(OrderStatus.PAID).totalPrice(918.0).build()
    );

    private final Map<String, Product> products = Map.of(
        "PRD-001", Product.builder()
            .productId("PRD-001").name("蓝牙耳机").category("数码").price(299.0).stock(50).build(),
        "PRD-002", Product.builder()
            .productId("PRD-002").name("机械键盘").category("数码").price(599.0).stock(20).build(),
        "PRD-003", Product.builder()
            .productId("PRD-003").name("运动跑鞋").category("运动").price(459.0).stock(100).build()
    );

    private final Map<String, User> users = Map.of(
        "USR-001", User.builder()
            .userId("USR-001").name("张三").vipLevel(VipLevel.NORMAL).orderCount(5).refundCount(0).build(),
        "USR-002", User.builder()
            .userId("USR-002").name("李四").vipLevel(VipLevel.VIP).orderCount(15).refundCount(1).build(),
        "USR-003", User.builder()
            .userId("USR-003").name("王五").vipLevel(VipLevel.NORMAL).orderCount(2).refundCount(4).build()
    );

    public Order getOrder(String orderId) {
        return orders.get(orderId);
    }

    public Product getProduct(String productId) {
        return products.get(productId);
    }

    public User getUser(String userId) {
        return users.get(userId);
    }

    /**
     * 根据订单ID反查用户
     */
    public User getUserByOrderId(String orderId) {
        Order order = orders.get(orderId);
        if (order == null) return null;
        return users.get(order.getUserId());
    }
}
