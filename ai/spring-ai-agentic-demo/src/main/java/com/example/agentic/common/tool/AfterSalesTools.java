package com.example.agentic.common.tool;

import com.example.agentic.common.model.Order;
import com.example.agentic.common.model.Product;
import com.example.agentic.common.model.User;
import com.example.agentic.common.service.MockDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 电商售后工具集 — 供 LLM Function Calling 使用。
 * <p>
 * 在 Spring AI 中，这些方法通过 {@link AfterSalesToolConfig} 注册为 FunctionCallback，
 * 而非 LangChain4j 的 @Tool 注解方式。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AfterSalesTools {

    private final MockDataService mockDataService;

    /**
     * 查询订单信息，包括订单号、商品、状态、金额等
     */
    public String queryOrder(String orderId) {
        log.info("[Tool] queryOrder called: orderId={}", orderId);
        Order order = mockDataService.getOrder(orderId);
        if (order == null) {
            return "未找到订单: " + orderId;
        }
        return String.format("订单ID: %s, 用户ID: %s, 商品: %s, 状态: %s, 总价: %.2f",
            order.getOrderId(), order.getUserId(),
            order.getItems().stream()
                .map(i -> i.getProductName() + "x" + i.getQuantity())
                .reduce((a, b) -> a + "," + b).orElse("无"),
            order.getStatus(), order.getTotalPrice());
    }

    /**
     * 查询商品信息，包括名称、分类、价格、库存
     */
    public String queryProduct(String productId) {
        log.info("[Tool] queryProduct called: productId={}", productId);
        Product product = mockDataService.getProduct(productId);
        if (product == null) {
            return "未找到商品: " + productId;
        }
        return String.format("商品ID: %s, 名称: %s, 分类: %s, 价格: %.2f, 库存: %d",
            product.getProductId(), product.getName(), product.getCategory(),
            product.getPrice(), product.getStock());
    }

    /**
     * 查询用户信息，包括VIP等级、历史订单数、历史退款次数
     */
    public String queryUser(String userId) {
        log.info("[Tool] queryUser called: userId={}", userId);
        User user = mockDataService.getUser(userId);
        if (user == null) {
            return "未找到用户: " + userId;
        }
        return String.format("用户ID: %s, 姓名: %s, VIP等级: %s, 历史订单数: %d, 历史退款次数: %d",
            user.getUserId(), user.getName(), user.getVipLevel(),
            user.getOrderCount(), user.getRefundCount());
    }

    /**
     * 根据订单ID查询下单用户的信息
     */
    public String queryUserByOrder(String orderId) {
        log.info("[Tool] queryUserByOrder called: orderId={}", orderId);
        User user = mockDataService.getUserByOrderId(orderId);
        if (user == null) {
            return "未找到该订单对应的用户";
        }
        return String.format("用户ID: %s, 姓名: %s, VIP等级: %s, 历史订单数: %d, 历史退款次数: %d",
            user.getUserId(), user.getName(), user.getVipLevel(),
            user.getOrderCount(), user.getRefundCount());
    }
}
