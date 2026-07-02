package com.example.agentic.sequential.service;

import com.example.agentic.common.tool.AfterSalesTools;
import com.example.agentic.sequential.RefundWorkflow;
import com.example.agentic.sequential.agent.FinanceAgent;
import com.example.agentic.sequential.agent.RiskAgent;
import com.example.agentic.sequential.agent.ServiceAgent;
import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.model.chat.ChatModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Sequential 退款审批工作流服务：Risk → Finance → Service 三步顺序链。
 */
@Slf4j
@Service
public class SequentialService {

    private final RefundWorkflow refundWorkflow;

    @Autowired
    public SequentialService(ChatModel chatModel, AfterSalesTools tools) {
        log.info("Building SequentialWorkflow with chatModel={}", chatModel);

        // 1. 构建三个子 Agent
        RiskAgent riskAgent = AgenticServices
                .agentBuilder(RiskAgent.class)
                .chatModel(chatModel)
                .tools(tools)
                .outputKey("riskResult")
                .build();
        log.info("RiskAgent built: {}", riskAgent);

        FinanceAgent financeAgent = AgenticServices
                .agentBuilder(FinanceAgent.class)
                .chatModel(chatModel)
                .tools(tools)
                .outputKey("financeResult")
                .build();
        log.info("FinanceAgent built: {}", financeAgent);

        ServiceAgent serviceAgent = AgenticServices
                .agentBuilder(ServiceAgent.class)
                .chatModel(chatModel)
                .tools(tools)
                .outputKey("finalResult")
                .build();
        log.info("ServiceAgent built: {}", serviceAgent);

        // 2. 组装顺序工作流
        this.refundWorkflow = AgenticServices
                .sequenceBuilder(RefundWorkflow.class)
                .subAgents(riskAgent, financeAgent, serviceAgent)
                .outputKey("finalResult")
                .build();
        log.info("SequentialWorkflow built: {}", refundWorkflow);
    }

    /**
     * 执行顺序退款审批工作流
     */
    public String processRefund(String orderId, String reason) {
        log.info("processRefund: orderId={}, reason={}", orderId, reason);
        return refundWorkflow.processRefund(orderId, reason);
    }
}
