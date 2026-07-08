package com.example.agentic.sequential.service;

import com.example.agentic.common.config.WorkflowContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Service;

/**
 * Sequential 退款审批工作流服务：Risk → Finance → Service 三步顺序链。
 * <p>
 * Spring AI 迁移说明：
 * - LangChain4j 的 @Agent 接口 + AgenticServices.agentBuilder() → ChatClient + System Prompt
 * - LangChain4j 的 sequenceBuilder → 手动顺序调用 ChatClient
 * - LangChain4j 的 outputKey + AgenticScope → WorkflowContext
 */
@Slf4j
@Service
public class SequentialService {

    private final ChatClient chatClient;
    private final ToolCallback[] toolCallbacks;

    public SequentialService(ChatClient.Builder chatClientBuilder,
                             ToolCallback[] toolCallbacks) {
        this.chatClient = chatClientBuilder.build();
        this.toolCallbacks = toolCallbacks;
        log.info("SequentialService initialized with {} tools", toolCallbacks.length);
    }

    /**
     * 执行顺序退款审批工作流
     */
    public String processRefund(String orderId, String reason) {
        log.info("processRefund: orderId={}, reason={}", orderId, reason);
        WorkflowContext context = new WorkflowContext();
        context.put("orderId", orderId);
        context.put("reason", reason);

        // Step 1: Risk Agent — 风控检查
        String riskResult = callRiskAgent(orderId, reason);
        context.put("riskResult", riskResult);
        log.info("RiskAgent result: {}", riskResult);

        // Step 2: Finance Agent — 财务核算
        String financeResult = callFinanceAgent(orderId, riskResult);
        context.put("financeResult", financeResult);
        log.info("FinanceAgent result: {}", financeResult);

        // Step 3: Service Agent — 客服确认
        String finalResult = callServiceAgent(orderId, reason, riskResult, financeResult);
        context.put("finalResult", finalResult);
        log.info("ServiceAgent result: {}", finalResult);

        return finalResult;
    }

    private String callRiskAgent(String orderId, String reason) {
        String systemPrompt = """
            你是电商售后风控专员。请根据以下退款申请信息进行风控评估。
            
            请先调用工具查询订单和用户信息，然后根据以下规则评估风险：
            
            1. 高风险(HIGH)：用户历史退款次数≥3次、订单金额>500且退款原因为"不喜欢了"
            2. 中风险(MEDIUM)：用户历史退款次数1-2次、订单已签收且退款原因为个人原因
            3. 低风险(LOW)：首次退款、质量问题、VIP用户
            
            请返回评估结果，格式如下：
            风险等级: [HIGH/MEDIUM/LOW]
            风险评分: [0-100的数字，越高越危险]
            风险标签: [欺诈嫌疑/高频退款/正常退款/质量问题]
            风控建议: [对下一步处理的建议]
            """;

        String userMessage = "订单ID: %s\n退款原因: %s".formatted(orderId, reason);

        return chatClient.prompt()
                .system(systemPrompt)
                .user(userMessage)
                .tools(toolCallbacks)
                .call()
                .content();
    }

    private String callFinanceAgent(String orderId, String riskResult) {
        String systemPrompt = """
            你是电商售后财务专员。请根据风控评估结果核算退款金额。
            
            请先调用工具查询订单详情，然后根据以下规则核算退款：
            
            1. 风控等级为LOW：全额退款（订单总价）
            2. 风控等级为MEDIUM：退款80%，扣除20%手续费
            3. 风控等级为HIGH：退款50%，扣除50%违约金
            
            请返回核算结果，格式如下：
            订单金额: [原订单金额]
            退款金额: [实际退款金额]
            扣除金额: [扣除的金额]
            扣除原因: [手续费/违约金/无扣除]
            核算说明: [简要说明核算逻辑]
            """;

        String userMessage = "订单ID: %s\n风控结果: %s".formatted(orderId, riskResult);

        return chatClient.prompt()
                .system(systemPrompt)
                .user(userMessage)
                .tools(toolCallbacks)
                .call()
                .content();
    }

    private String callServiceAgent(String orderId, String reason, String riskResult, String financeResult) {
        String systemPrompt = """
            你是电商售后客服主管。请根据风控评估和财务核算结果，生成最终处理方案。
            
            请综合以上信息，做出最终处理决定：
            
            1. 如果风控为LOW且财务全额退款 → 直接批准全额退款
            2. 如果风控为MEDIUM → 批准部分退款，同时给用户发送补偿优惠券
            3. 如果风控为HIGH → 需要进一步审核，暂时拒绝退款但保留申诉通道
            
            请返回最终结果，格式如下：
            处理结果: [APPROVED/PARTIAL_REFUND/REJECTED]
            最终退款金额: [数字]
            处理说明: [详细说明处理决定的原因]
            用户通知: [发给用户的退款通知文案，语气友好专业]
            """;

        String userMessage = "订单ID: %s\n退款原因: %s\n风控结果: %s\n财务核算结果: %s"
                .formatted(orderId, reason, riskResult, financeResult);

        return chatClient.prompt()
                .system(systemPrompt)
                .user(userMessage)
                .tools(toolCallbacks)
                .call()
                .content();
    }
}
