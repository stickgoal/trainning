package com.example.agentic.common.tool;

import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.ai.tool.metadata.ToolMetadata;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring AI Function Callback 配置。
 * <p>
 * 将 AfterSalesTools 的方法注册为 LLM 可调用的 Function Calling 工具。
 * 对应 LangChain4j 中 @Tool 注解的功能。
 */
@Configuration
public class AfterSalesToolConfig {

    @Bean
    public FunctionToolCallback queryOrderTool(AfterSalesTools tools) {
        return FunctionToolCallback.builder("queryOrder", (String orderId) -> tools.queryOrder(orderId))
                .description("查询订单信息，包括订单号、商品、状态、金额等。参数：orderId - 订单ID，如 ORD-001")
                .inputType(String.class)
                .build();
    }

    @Bean
    public FunctionToolCallback queryProductTool(AfterSalesTools tools) {
        return FunctionToolCallback.builder("queryProduct", (String productId) -> tools.queryProduct(productId))
                .description("查询商品信息，包括名称、分类、价格、库存。参数：productId - 商品ID，如 PRD-001")
                .inputType(String.class)
                .build();
    }

    @Bean
    public FunctionToolCallback queryUserTool(AfterSalesTools tools) {
        return FunctionToolCallback.builder("queryUser", (String userId) -> tools.queryUser(userId))
                .description("查询用户信息，包括VIP等级、历史订单数、历史退款次数。参数：userId - 用户ID，如 USR-001")
                .inputType(String.class)
                .build();
    }

    @Bean
    public FunctionToolCallback queryUserByOrderTool(AfterSalesTools tools) {
        return FunctionToolCallback.builder("queryUserByOrder", (String orderId) -> tools.queryUserByOrder(orderId))
                .description("根据订单ID查询下单用户的信息。参数：orderId - 订单ID")
                .inputType(String.class)
                .build();
    }
}
