package com.example.agentic.supervisor.service;

import com.example.agentic.common.tool.AfterSalesTools;
import com.example.agentic.supervisor.SupervisorWorkflow;
import com.example.agentic.supervisor.agent.EmotionAgent;
import com.example.agentic.supervisor.agent.FactAgent;
import com.example.agentic.supervisor.agent.NotifyAgent;
import com.example.agentic.supervisor.agent.SolutionAgent;
import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.agentic.scope.AgenticScope;
import dev.langchain4j.agentic.supervisor.SupervisorResponseStrategy;
import dev.langchain4j.model.chat.ChatModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Function;

/**
 * Supervisor 主管调度工作流服务。
 * 
 * 与 Sequential/Conditional 不同，Supervisor 模式由 LLM 驱动的 Supervisor Agent
 * 动态决定调用哪些子 Agent、按什么顺序执行。
 * 
 * 可用子 Agent：
 *   - EmotionAgent：情绪安抚
 *   - FactAgent：事实调查
 *   - SolutionAgent：方案制定
 *   - NotifyAgent：通知执行
 * 
 * Supervisor 会根据用户诉求自主编排执行计划。
 */
@Slf4j
@Service
public class SupervisorService {

    private final SupervisorWorkflow supervisorWorkflow;

    @Autowired
    public SupervisorService(ChatModel chatModel, AfterSalesTools tools) {
        log.info("Building SupervisorWorkflow with chatModel={}", chatModel);

        // 1. 构建四个子 Agent
        EmotionAgent emotionAgent = AgenticServices
                .agentBuilder(EmotionAgent.class)
                .chatModel(chatModel)
                .tools(tools)
                .outputKey("emotionResult")
                .build();
        log.info("EmotionAgent built: {}", emotionAgent);

        FactAgent factAgent = AgenticServices
                .agentBuilder(FactAgent.class)
                .chatModel(chatModel)
                .tools(tools)
                .outputKey("factResult")
                .build();
        log.info("FactAgent built: {}", factAgent);

        SolutionAgent solutionAgent = AgenticServices
                .agentBuilder(SolutionAgent.class)
                .chatModel(chatModel)
                .outputKey("solutionResult")
                .build();
        log.info("SolutionAgent built: {}", solutionAgent);

        NotifyAgent notifyAgent = AgenticServices
                .agentBuilder(NotifyAgent.class)
                .chatModel(chatModel)
                .outputKey("notifyResult")
                .build();
        log.info("NotifyAgent built: {}", notifyAgent);

        // 2. Supervisor 上下文：告诉 Supervisor 它有哪些子 Agent 可以调度
        String supervisorContext = """
            你是电商售后主管，负责协调处理复杂的用户投诉。
            
            你可以调度以下子 Agent：
            1. EmotionAgent - 情绪安抚：分析用户情绪，生成安抚话术
            2. FactAgent - 事实调查：查询订单和用户信息，梳理事实
            3. SolutionAgent - 方案制定：综合情绪和事实，制定处理方案
            4. NotifyAgent - 通知执行：生成客户通知文案并执行
            
            请根据用户投诉内容，自主决定：
            - 需要调用哪些子 Agent
            - 调用顺序
            - 是否需要重复调用某个 Agent
            - 何时完成处理
            
            通常的处理流程是：情绪安抚 → 事实调查 → 方案制定 → 通知执行，
            但你可以根据具体情况灵活调整。
            """;

        // 3. 请求生成器：从方法参数生成 Supervisor 的初始输入
        Function<AgenticScope, String> requestGenerator = scope -> {
            String orderId = scope.readState("orderId", "");
            String complaint = scope.readState("complaint", "");
            return "请处理以下售后投诉：\n订单ID: " + orderId + "\n用户投诉: " + complaint;
        };

        // 4. 输出提取器：从 AgenticScope 中提取最终结果
        Function<AgenticScope, Object> outputExtractor = scope -> {
            // 优先返回 notifyResult，其次 solutionResult
            String notifyResult = scope.readState("notifyResult", "");
            if (notifyResult != null && !notifyResult.isEmpty()) {
                return notifyResult;
            }
            return scope.readState("solutionResult", "处理未完成");
        };

        // 5. 构建 Supervisor 工作流
        this.supervisorWorkflow = AgenticServices
                .supervisorBuilder(SupervisorWorkflow.class)
                .chatModel(chatModel)
                .name("AfterSalesSupervisor")
                .description("电商售后主管，动态调度子Agent处理复杂投诉")
                .supervisorContext(supervisorContext)
                .requestGenerator(requestGenerator)
                .subAgents(emotionAgent, factAgent, solutionAgent, notifyAgent)
                .maxAgentsInvocations(8)
                .output(outputExtractor)
                .outputKey("finalResult")
                .build();
        log.info("SupervisorWorkflow built: {}", supervisorWorkflow);
    }

    /**
     * 执行 Supervisor 工作流处理投诉
     */
    public String handleComplaint(String orderId, String complaint) {
        log.info("handleComplaint: orderId={}, complaint={}", orderId, complaint);
        return supervisorWorkflow.handleComplaint(orderId, complaint);
    }
}
