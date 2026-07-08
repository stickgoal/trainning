package com.example.agentic.conditional.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Service;

/**
 * Conditional 条件分流工作流服务。
 * <p>
 * Spring AI 迁移说明：
 * - LangChain4j 的 conditionalBuilder → if/else 条件路由
 * - ClassifyAgent 分类后，根据结果选择不同的处理 Agent
 * <p>
 * 流程：
 *   ClassifyAgent（分类）→ 条件路由:
 *     QUALITY_ISSUE     → AutoRefundAgent（快速退款）
 *     PERSONAL_REASON   → ManualReviewAgent（人工审核）
 *     EXCHANGE_REQUEST  → ExchangeAgent（换货处理）
 */
@Slf4j
@Service
public class ConditionalService {

    private final ChatClient chatClient;
    private final ToolCallback[] toolCallbacks;

    public ConditionalService(ChatClient.Builder chatClientBuilder,
                              ToolCallback[] toolCallbacks) {
        this.chatClient = chatClientBuilder.build();
        this.toolCallbacks = toolCallbacks;
        log.info("ConditionalService initialized");
    }

    /**
     * 执行条件分流退款工作流
     */
    public String processRefund(String orderId, String reason) {
        log.info("processRefund: orderId={}, reason={}", orderId, reason);

        // Step 1: Classify Agent — 原因分类
        String category = callClassifyAgent(orderId, reason);
        log.info("ClassifyAgent result: {}", category);

        // Step 2: 条件路由
        String processResult;
        if (category.contains("QUALITY_ISSUE")) {
            log.info("Routing to AutoRefundAgent (QUALITY_ISSUE)");
            processResult = callAutoRefundAgent(orderId, reason, category);
        } else if (category.contains("EXCHANGE_REQUEST")) {
            log.info("Routing to ExchangeAgent (EXCHANGE_REQUEST)");
            processResult = callExchangeAgent(orderId, reason, category);
        } else {
            // 默认走人工审核（PERSONAL_REASON 或其他）
            log.info("Routing to ManualReviewAgent (PERSONAL_REASON or default)");
            processResult = callManualReviewAgent(orderId, reason, category);
        }

        log.info("Process result: {}", processResult);
        return processResult;
    }

    private String callClassifyAgent(String orderId, String reason) {
        String systemPrompt = """
            你是电商售后分类专员。请分析用户的退款原因，将其归类为以下三种之一：
            
            1. QUALITY_ISSUE — 商品质量问题（如：坏了、有瑕疵、与描述不符、假货等）
            2. PERSONAL_REASON — 个人原因（如：不喜欢了、买错了、不需要了、太贵了等）
            3. EXCHANGE_REQUEST — 换货请求（如：想换个颜色、换个尺码、换个型号等）
            
            请先调用工具查询订单信息以辅助判断。
            
            请只返回分类标签，格式：
            分类: [QUALITY_ISSUE/PERSONAL_REASON/EXCHANGE_REQUEST]
            判断依据: [简要说明]
            """;

        return chatClient.prompt()
                .system(systemPrompt)
                .user("订单ID: %s\n退款原因: %s".formatted(orderId, reason))
                .tools(toolCallbacks)
                .call()
                .content();
    }

    private String callAutoRefundAgent(String orderId, String reason, String category) {
        String systemPrompt = """
            你是电商售后快速退款处理专员。该退款申请已被归类为质量问题，适用快速退款通道。
            
            请调用工具查询订单和用户信息，然后：
            1. 确认订单金额
            2. 执行全额退款
            3. 生成退款通知文案
            
            请返回处理结果，格式：
            处理通道: 快速退款
            退款金额: [全额]
            处理状态: 已提交
            用户通知: [退款通知文案]
            """;

        return chatClient.prompt()
                .system(systemPrompt)
                .user("订单ID: %s\n退款原因: %s\n分类结果: %s".formatted(orderId, reason, category))
                .tools(toolCallbacks)
                .call()
                .content();
    }

    private String callManualReviewAgent(String orderId, String reason, String category) {
        String systemPrompt = """
            你是电商售后人工审核协调员。该退款申请已被归类为个人原因，需要人工审核。
            
            请调用工具查询订单和用户信息，然后：
            1. 评估退款合理性
            2. 检查是否符合退款政策
            3. 生成审核意见和通知文案
            
            请返回处理结果，格式：
            处理通道: 人工审核
            审核意见: [建议批准/建议拒绝/需补充材料]
            退款比例: [建议退款百分比]
            用户通知: [审核通知文案]
            """;

        return chatClient.prompt()
                .system(systemPrompt)
                .user("订单ID: %s\n退款原因: %s\n分类结果: %s".formatted(orderId, reason, category))
                .tools(toolCallbacks)
                .call()
                .content();
    }

    private String callExchangeAgent(String orderId, String reason, String category) {
        String systemPrompt = """
            你是电商售后换货处理专员。该申请已被归类为换货请求。
            
            请调用工具查询订单和商品信息，然后：
            1. 确认原商品信息
            2. 检查库存是否有可换商品
            3. 生成换货处理方案
            
            请返回处理结果，格式：
            处理通道: 换货处理
            原商品: [商品信息]
            换货方案: [具体换货建议]
            库存状态: [有货/缺货]
            用户通知: [换货通知文案]
            """;

        return chatClient.prompt()
                .system(systemPrompt)
                .user("订单ID: %s\n退款原因: %s\n分类结果: %s".formatted(orderId, reason, category))
                .tools(toolCallbacks)
                .call()
                .content();
    }
}
