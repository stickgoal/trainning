package com.example.agentic.parallel.service;

import com.example.agentic.common.tool.AfterSalesTools;
import com.example.agentic.parallel.ParallelCheckWorkflow;
import com.example.agentic.parallel.agent.CreditCheckAgent;
import com.example.agentic.parallel.agent.OrderCheckAgent;
import com.example.agentic.parallel.agent.StockCheckAgent;
import com.example.agentic.parallel.agent.SummaryAgent;
import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.agentic.UntypedAgent;
import dev.langchain4j.model.chat.ChatModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Parallel 并行核验退款工作流服务。
 * 
 * 结构：先用 parallelBuilder 并行执行三个核验 Agent，
 * 再用 sequenceBuilder 将并行块和汇总 Agent 串联。
 * 
 * 流程：
 *   OrderCheckAgent ─┐
 *   CreditCheckAgent ─┤ parallel → SummaryAgent (sequence)
 *   StockCheckAgent  ─┘
 */
@Slf4j
@Service
public class ParallelService {

    private final ParallelCheckWorkflow parallelCheckWorkflow;

    @Autowired
    public ParallelService(ChatModel chatModel, AfterSalesTools tools) {
        log.info("Building ParallelWorkflow with chatModel={}", chatModel);

        // 1. 构建三个并行核验 Agent
        OrderCheckAgent orderCheckAgent = AgenticServices
                .agentBuilder(OrderCheckAgent.class)
                .chatModel(chatModel)
                .tools(tools)
                .outputKey("orderCheckResult")
                .build();
        log.info("OrderCheckAgent built: {}", orderCheckAgent);

        CreditCheckAgent creditCheckAgent = AgenticServices
                .agentBuilder(CreditCheckAgent.class)
                .chatModel(chatModel)
                .tools(tools)
                .outputKey("creditCheckResult")
                .build();
        log.info("CreditCheckAgent built: {}", creditCheckAgent);

        StockCheckAgent stockCheckAgent = AgenticServices
                .agentBuilder(StockCheckAgent.class)
                .chatModel(chatModel)
                .tools(tools)
                .outputKey("stockCheckResult")
                .build();
        log.info("StockCheckAgent built: {}", stockCheckAgent);

        // 2. 构建并行核验块
        UntypedAgent parallelCheckBlock = AgenticServices
                .parallelBuilder()
                .subAgents(orderCheckAgent, creditCheckAgent, stockCheckAgent)
                .outputKey("parallelCheckResult")
                .build();
        log.info("ParallelCheckBlock built: {}", parallelCheckBlock);

        // 3. 构建汇总 Agent
        SummaryAgent summaryAgent = AgenticServices
                .agentBuilder(SummaryAgent.class)
                .chatModel(chatModel)
                .outputKey("summaryResult")
                .build();
        log.info("SummaryAgent built: {}", summaryAgent);

        // 4. 串联：并行核验 → 汇总
        this.parallelCheckWorkflow = AgenticServices
                .sequenceBuilder(ParallelCheckWorkflow.class)
                .subAgents(parallelCheckBlock, summaryAgent)
                .outputKey("summaryResult")
                .build();
        log.info("ParallelCheckWorkflow built: {}", parallelCheckWorkflow);
    }

    /**
     * 执行并行核验退款工作流
     */
    public String parallelCheck(String orderId, String reason) {
        log.info("parallelCheck: orderId={}, reason={}", orderId, reason);
        return parallelCheckWorkflow.parallelCheck(orderId, reason);
    }
}
