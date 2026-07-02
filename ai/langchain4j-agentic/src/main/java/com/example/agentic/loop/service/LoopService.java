package com.example.agentic.loop.service;

import com.example.agentic.common.tool.AfterSalesTools;
import com.example.agentic.loop.LoopReplyWorkflow;
import com.example.agentic.loop.agent.DraftAgent;
import com.example.agentic.loop.agent.ReviewAgent;
import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.agentic.scope.AgenticScope;
import dev.langchain4j.model.chat.ChatModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Predicate;

/**
 * Loop 迭代优化工作流服务。
 * 
 * 流程：
 *   DraftAgent → ReviewAgent → 评分≥8则退出，否则重新起草 → 最多3次迭代
 * 
 * 退出条件：ReviewAgent 输出包含 "APPROVED"
 */
@Slf4j
@Service
public class LoopService {

    private final LoopReplyWorkflow loopReplyWorkflow;

    @Autowired
    public LoopService(ChatModel chatModel, AfterSalesTools tools) {
        log.info("Building LoopWorkflow with chatModel={}", chatModel);

        // 1. 构建 Draft Agent
        DraftAgent draftAgent = AgenticServices
                .agentBuilder(DraftAgent.class)
                .chatModel(chatModel)
                .tools(tools)
                .outputKey("draftReply")
                .build();
        log.info("DraftAgent built: {}", draftAgent);

        // 2. 构建 Review Agent
        ReviewAgent reviewAgent = AgenticServices
                .agentBuilder(ReviewAgent.class)
                .chatModel(chatModel)
                .outputKey("reviewResult")
                .build();
        log.info("ReviewAgent built: {}", reviewAgent);

        // 3. 退出条件：reviewResult 包含 "APPROVED"
        Predicate<AgenticScope> exitCondition = scope -> {
            String reviewResult = scope.readState("reviewResult", "");
            boolean approved = reviewResult.contains("APPROVED");
            log.info("Loop exit condition check: reviewResult contains APPROVED = {}", approved);
            return approved;
        };

        // 4. 组装 Loop 工作流
        this.loopReplyWorkflow = AgenticServices
                .loopBuilder(LoopReplyWorkflow.class)
                .subAgents(draftAgent, reviewAgent)
                .exitCondition(exitCondition)
                .maxIterations(3)
                .outputKey("reviewResult")
                .build();
        log.info("LoopReplyWorkflow built: {}", loopReplyWorkflow);
    }

    /**
     * 执行迭代优化售后回复文案工作流
     */
    public String generateReply(String orderId, String reason) {
        log.info("generateReply: orderId={}, reason={}", orderId, reason);
        return loopReplyWorkflow.generateReply(orderId, reason);
    }
}
