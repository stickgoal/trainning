package com.example.agentic.mcp;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * MCP Server 售后工具集。
 * <p>
 * 使用 Spring AI 的 @Tool 注解暴露给 MCP Client。
 * 功能与主应用的 AfterSalesTools 完全一致，但作为独立进程运行。
 */
@Service
public class AfterSalesMcpTools {

    // ── 内嵌模拟数据（MCP Server 是独立进程，不依赖主应用） ──

    private static final Map<String, String> ORDERS = Map.of(
        "ORD-001", "订单ID: ORD-001, 用户ID: USR-001, 商品: 蓝牙耳机x1, 状态: DELIVERED, 总价: 299.00",
        "ORD-002", "订单ID: ORD-002, 用户ID: USR-002, 商品: 机械键盘x1, 状态: SHIPPED, 总价: 599.00",
        "ORD-003", "订单ID: ORD-003, 用户ID: USR-003, 商品: 运动跑鞋x2, 状态: PAID, 总价: 918.00"
    );

    private static final Map<String, String> PRODUCTS = Map.of(
        "PRD-001", "商品ID: PRD-001, 名称: 蓝牙耳机, 分类: 数码, 价格: 299.00, 库存: 50",
        "PRD-002", "商品ID: PRD-002, 名称: 机械键盘, 分类: 数码, 价格: 599.00, 库存: 20",
        "PRD-003", "商品ID: PRD-003, 名称: 运动跑鞋, 分类: 运动, 价格: 459.00, 库存: 100"
    );

    private static final Map<String, String> USERS = Map.of(
        "USR-001", "用户ID: USR-001, 姓名: 张三, VIP等级: NORMAL, 历史订单数: 5, 历史退款次数: 0",
        "USR-002", "用户ID: USR-002, 姓名: 李四, VIP等级: VIP, 历史订单数: 15, 历史退款次数: 1",
        "USR-003", "用户ID: USR-003, 姓名: 王五, VIP等级: NORMAL, 历史订单数: 2, 历史退款次数: 4"
    );

    private static final Map<String, String> ORDER_TO_USER = Map.of(
        "ORD-001", "USR-001",
        "ORD-002", "USR-002",
        "ORD-003", "USR-003"
    );

    @Tool(name = "queryOrder", description = "查询订单信息，包括订单号、商品、状态、金额等")
    public String queryOrder(
            @ToolParam(description = "订单ID，如 ORD-001") String orderId) {
        return ORDERS.getOrDefault(orderId, "未找到订单: " + orderId);
    }

    @Tool(name = "queryProduct", description = "查询商品信息，包括名称、分类、价格、库存")
    public String queryProduct(
            @ToolParam(description = "商品ID，如 PRD-001") String productId) {
        return PRODUCTS.getOrDefault(productId, "未找到商品: " + productId);
    }

    @Tool(name = "queryUser", description = "查询用户信息，包括VIP等级、历史订单数、历史退款次数")
    public String queryUser(
            @ToolParam(description = "用户ID，如 USR-001") String userId) {
        return USERS.getOrDefault(userId, "未找到用户: " + userId);
    }

    @Tool(name = "queryUserByOrder", description = "根据订单ID查询下单用户的信息")
    public String queryUserByOrder(
            @ToolParam(description = "订单ID") String orderId) {
        String userId = ORDER_TO_USER.get(orderId);
        if (userId == null) return "未找到该订单对应的用户";
        return USERS.getOrDefault(userId, "未找到用户: " + userId);
    }
}
