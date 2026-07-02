package com.example.agentic.conditional.service;

import com.example.agentic.common.tool.AfterSalesTools;
import com.example.agentic.conditional.ConditionalRefundWorkflow;
import com.example.agentic.conditional.agent.AutoRefundAgent;
import com.example.agentic.conditional.agent.ClassifyAgent;
import com.example.agentic.conditional.agent.ExchangeAgent;
import com.example.agentic.conditional.agent.ManualReviewAgent;
import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.agentic.UntypedAgent;
import dev.langchain4j.agentic.scope.AgenticScope;
import dev.langchain4j.model.chat.ChatModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Predicate;

/**
 * Conditional 条件分流工作流服务。
 * 
 * 流程：
 *   ClassifyAgent（分类）→ 条件路由:
 *     refundCategory 含 QUALITY_ISSUE     → AutoRefundAgent（快速退款）
 *     refundCategory 含 PERSONAL_REASON   → ManualReviewAgent（人工审核）
 *     refundCategory 含 EXCHANGE_REQUEST  → ExchangeAgent（换货处理）
 * 
 * 实现：sequenceBuilder 串联 ClassifyAgent + conditionalBuilder
 */
@Slf4j
@Service
public class ConditionalService {

    private final ConditionalRefundWorkflow conditionalRefundWorkflow;

    @Autowired
    public ConditionalService(ChatModel chatModel, AfterSalesTools tools) {
        log.info("Building ConditionalWorkflow with chatModel={}", chatModel);

        // 1. 分类 Agent
        ClassifyAgent classifyAgent = AgenticServices
                .agentBuilder(ClassifyAgent.class)
                .chatModel(chatModel)
                .tools(tools)
                .outputKey("refundCategory")
                .build();
        log.info("ClassifyAgent built: {}", classifyAgent);

        // 2. 三个分支 Agent
        AutoRefundAgent autoRefundAgent = AgenticServices
                .agentBuilder(AutoRefundAgent.class)
                .chatModel(chatModel)
                .tools(tools)
                .outputKey("processResult")
                .build();
        log.info("AutoRefundAgent built: {}", autoRefundAgent);

        ManualReviewAgent manualReviewAgent = AgenticServices
                .agentBuilder(ManualReviewAgent.class)
                .chatModel(chatModel)
                .tools(tools)
                .outputKey("processResult")
                .build();
        log.info("ManualReviewAgent built: {}", manualReviewAgent);

        ExchangeAgent exchangeAgent = AgenticServices
                .agentBuilder(ExchangeAgent.class)
                .chatModel(chatModel)
                .tools(tools)
                .outputKey("processResult")
                .build();
        log.info("ExchangeAgent built: {}", exchangeAgent);

        // 3. 条件路由：根据 refundCategory 选择分支
        Predicate<AgenticScope> isQuality = scope -> {
            String category = scope.readState("refundCategory", "");
            log.info("Conditional routing: refundCategory='{}'", category);
            return category.contains("QUALITY_ISSUE");
        };
        Predicate<AgenticScope> isPersonal = scope -> {
            String category = scope.readState("refundCategory", "");
            return category.contains("PERSONAL_REASON");
        };
        Predicate<AgenticScope> isExchange = scope -> {
            String category = scope.readState("refundCategory", "");
            return category.contains("EXCHANGE_REQUEST");
        };

        // 4. 构建 conditional 块
        UntypedAgent conditionalBlock = AgenticServices
                .conditionalBuilder()
                .subAgents(isQuality, autoRefundAgent)
                .subAgents(isPersonal, manualReviewAgent)
                .subAgents(isExchange, exchangeAgent)
                .outputKey("processResult")
                .build();
        log.info("ConditionalBlock built: {}", conditionalBlock);

        // 5. 串联：分类 → 条件路由
        this.conditionalRefundWorkflow = AgenticServices
                .sequenceBuilder(ConditionalRefundWorkflow.class)
                .subAgents(classifyAgent, conditionalBlock)
                .outputKey("processResult")
                .build();
        log.info("ConditionalRefundWorkflow built: {}", conditionalRefundWorkflow);
    }

    /**
     * 执行条件分流退款工作流
     */
    public String processRefund(String orderId, String reason) {
        log.info("processRefund: orderId={}, reason={}", orderId, reason);
        return conditionalRefundWorkflow.processRefund(orderId, reason);
    }
}
