package com.example.agentic.supervisor.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Service;

/**
 * Supervisor 主管调度工作流服务。
 * <p>
 * Spring AI 迁移说明：
 * - LangChain4j 的 supervisorBuilder → ChatClient + System Prompt + Function Calling
 * - Supervisor 通过 LLM 自主决定调用哪些子 Agent、按什么顺序执行
 * - 子 Agent 作为 Function Callback 注册给 Supervisor 的 ChatClient
 * <p>
 * 与 Sequential/Conditional 不同，Supervisor 模式由 LLM 驱动的 Supervisor Agent
 * 动态决定调用哪些子 Agent、按什么顺序执行。
 */
@Slf4j
@Service
public class SupervisorService {

    private final ChatClient chatClient;
    private final ToolCallback[] toolCallbacks;

    public SupervisorService(ChatClient.Builder chatClientBuilder,
                             ToolCallback[] toolCallbacks) {
        this.chatClient = chatClientBuilder.build();
        this.toolCallbacks = toolCallbacks;
        log.info("SupervisorService initialized");
    }

    /**
     * 执行 Supervisor 工作流处理投诉
     */
    public String handleComplaint(String orderId, String complaint) {
        log.info("handleComplaint: orderId={}, complaint={}", orderId, complaint);

        // Supervisor 的 system prompt 描述了它可以调度的子 Agent 及其职责
        // 在 Spring AI 中，子 Agent 的能力通过更丰富的 tool callbacks 暴露给 LLM
        // Supervisor LLM 自主决定调用顺序和时机
        String systemPrompt = """
            你是电商售后主管，负责协调处理复杂的用户投诉。
            
            你可以使用以下工具来完成处理：
            - queryOrder: 查询订单信息
            - queryProduct: 查询商品信息
            - queryUser: 查询用户信息
            - queryUserByOrder: 根据订单查询用户
            - emotionAnalysis: 分析用户情绪并生成安抚话术
            - factInvestigation: 调查订单事实和历史记录
            - solutionDesign: 制定赔偿/解决方案
            - notificationDraft: 生成客户通知文案
            
            请根据用户投诉内容，自主决定：
            - 需要调用哪些工具
            - 调用顺序
            - 是否需要重复调用某个工具
            - 何时完成处理
            
            通常的处理流程是：情绪安抚 → 事实调查 → 方案制定 → 通知执行，
            但你可以根据具体情况灵活调整。
            
            最终请输出一份完整的处理结果，包含：
            1. 情绪安抚措施
            2. 事实调查结论
            3. 解决方案
            4. 客户通知文案
            """;

        String userMessage = "请处理以下售后投诉：\n订单ID: %s\n用户投诉: %s".formatted(orderId, complaint);

        String result = chatClient.prompt()
                .system(systemPrompt)
                .user(userMessage)
                .tools(toolCallbacks)
                .call()
                .content();

        log.info("Supervisor workflow completed");
        return result;
    }
}
