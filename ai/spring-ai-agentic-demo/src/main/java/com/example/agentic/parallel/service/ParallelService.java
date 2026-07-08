package com.example.agentic.parallel.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Parallel 并行核验退款工作流服务。
 * <p>
 * Spring AI 迁移说明：
 * - LangChain4j 的 parallelBuilder → CompletableFuture 并行调用 ChatClient
 * - 三个核验 Agent 并行执行，结果汇总后交给 SummaryAgent
 * <p>
 * 流程：
 *   OrderCheckAgent ─┐
 *   CreditCheckAgent ─┤ parallel → SummaryAgent
 *   StockCheckAgent  ─┘
 */
@Slf4j
@Service
public class ParallelService {

    private final ChatClient chatClient;
    private final ToolCallback[] toolCallbacks;
    private final ExecutorService executor = Executors.newFixedThreadPool(3);

    public ParallelService(ChatClient.Builder chatClientBuilder,
                           ToolCallback[] toolCallbacks) {
        this.chatClient = chatClientBuilder.build();
        this.toolCallbacks = toolCallbacks;
        log.info("ParallelService initialized");
    }

    /**
     * 执行并行核验退款工作流
     */
    public String parallelCheck(String orderId, String reason) {
        log.info("parallelCheck: orderId={}, reason={}", orderId, reason);

        // 并行执行三个核验 Agent
        CompletableFuture<String> orderCheckFuture = CompletableFuture.supplyAsync(
                () -> callOrderCheckAgent(orderId, reason), executor);
        CompletableFuture<String> creditCheckFuture = CompletableFuture.supplyAsync(
                () -> callCreditCheckAgent(orderId, reason), executor);
        CompletableFuture<String> stockCheckFuture = CompletableFuture.supplyAsync(
                () -> callStockCheckAgent(orderId, reason), executor);

        // 等待所有并行任务完成
        CompletableFuture.allOf(orderCheckFuture, creditCheckFuture, stockCheckFuture).join();

        String orderCheckResult = orderCheckFuture.join();
        String creditCheckResult = creditCheckFuture.join();
        String stockCheckResult = stockCheckFuture.join();

        log.info("OrderCheck result: {}", orderCheckResult);
        log.info("CreditCheck result: {}", creditCheckResult);
        log.info("StockCheck result: {}", stockCheckResult);

        // 汇总 Agent
        String summaryResult = callSummaryAgent(orderId, reason,
                orderCheckResult, creditCheckResult, stockCheckResult);
        log.info("Summary result: {}", summaryResult);

        return summaryResult;
    }

    private String callOrderCheckAgent(String orderId, String reason) {
        String systemPrompt = """
            你是电商售后订单核验专员。请核实订单状态和签收情况。
            
            请调用工具查询订单信息，然后评估：
            1. 订单是否存在且有效
            2. 订单当前状态（已支付/运输中/已签收）
            3. 是否在可退款时间窗口内
            4. 商品是否已签收（影响退款流程）
            
            请返回核验结果，格式：
            订单状态: [状态]
            签收情况: [已签收/未签收]
            可退款: [是/否]
            核验说明: [详细说明]
            """;

        return chatClient.prompt()
                .system(systemPrompt)
                .user("订单ID: %s\n退款原因: %s".formatted(orderId, reason))
                .tools(toolCallbacks)
                .call()
                .content();
    }

    private String callCreditCheckAgent(String orderId, String reason) {
        String systemPrompt = """
            你是电商售后信用评估专员。请评估用户的信用等级和退款历史。
            
            请调用工具查询用户信息，然后评估：
            1. 用户VIP等级
            2. 历史订单数量和退款次数
            3. 退款频率是否异常
            4. 信用风险等级
            
            请返回信用评估结果，格式：
            用户等级: [VIP等级]
            退款频率: [正常/偏高/异常]
            信用风险: [低/中/高]
            评估说明: [详细说明]
            """;

        return chatClient.prompt()
                .system(systemPrompt)
                .user("订单ID: %s\n退款原因: %s".formatted(orderId, reason))
                .tools(toolCallbacks)
                .call()
                .content();
    }

    private String callStockCheckAgent(String orderId, String reason) {
        String systemPrompt = """
            你是电商售后库存检查专员。请检查相关商品的库存状况。
            
            请调用工具查询订单和商品信息，然后评估：
            1. 商品当前库存量
            2. 退货后库存是否充足
            3. 商品是否为热销/紧缺商品
            4. 换货可行性
            
            请返回库存检查结果，格式：
            商品库存: [数量]
            库存状态: [充足/紧张/缺货]
            换货可行: [是/否]
            检查说明: [详细说明]
            """;

        return chatClient.prompt()
                .system(systemPrompt)
                .user("订单ID: %s\n退款原因: %s".formatted(orderId, reason))
                .tools(toolCallbacks)
                .call()
                .content();
    }

    private String callSummaryAgent(String orderId, String reason,
                                     String orderCheckResult, String creditCheckResult, String stockCheckResult) {
        String systemPrompt = """
            你是电商售后综合评估主管。请汇总三个维度的核验结果，做出最终决策。
            
            请综合分析订单核验、信用评估、库存检查三方面结果，给出：
            1. 综合评估结论
            2. 建议处理方式（全额退款/部分退款/换货/拒绝）
            3. 风险提示
            4. 给客户的回复建议
            
            请返回综合评估结果，格式：
            综合结论: [通过/需审核/拒绝]
            建议处理: [具体处理方式]
            风险提示: [如有]
            客户回复: [建议回复文案]
            """;

        String userMessage = """
            订单ID: %s
            退款原因: %s
            
            === 订单核验结果 ===
            %s
            
            === 信用评估结果 ===
            %s
            
            === 库存检查结果 ===
            %s
            """.formatted(orderId, reason, orderCheckResult, creditCheckResult, stockCheckResult);

        return chatClient.prompt()
                .system(systemPrompt)
                .user(userMessage)
                .call()
                .content();
    }
}
