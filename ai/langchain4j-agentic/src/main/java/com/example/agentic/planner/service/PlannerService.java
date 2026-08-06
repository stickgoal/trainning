package com.example.agentic.planner.service;

import com.example.agentic.common.tool.AfterSalesTools;
import com.example.agentic.planner.PlannerWorkflow;
import com.example.agentic.planner.agent.PlannerExecuteAgent;
import com.example.agentic.planner.agent.PlanAgent;
import com.example.agentic.planner.agent.ProgressAgent;
import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.agentic.UntypedAgent;
import dev.langchain4j.agentic.scope.AgenticScope;
import dev.langchain4j.model.chat.ChatModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Predicate;

/**
 * Planner 规划-执行工作流服务。
 * 
 * <h3>核心设计：先规划，后执行</h3>
 * 
 * <pre>
 * Sequence:
 *   ├── PlanAgent（生成结构化 JSON 计划 → plan）
 *   └── Loop（exitCondition: 所有步骤 completed）
 *        ├── PlannerExecuteAgent（处理下一个 PENDING 步骤，读写同一个 plan 状态 → plan）
 *        └── ProgressAgent（检查进度 → progressResult）
 * </pre>
 * 
 * <h3>与 Supervisor 的本质区别</h3>
 * <ul>
 *   <li>Supervisor: LLM 运行时动态决策，黑盒编排</li>
 *   <li>Planner: 先生成完整计划（白盒可审查），再逐步执行</li>
 * </ul>
 * 
 * <p>循环机制：PlannerExecuteAgent 每轮处理一个工单并覆写 "plan" 状态（已处理步骤标记 COMPLETED），
 * ProgressAgent 检查是否全部完成。下一轮 PlannerExecuteAgent 通过 @V("plan") 读取累积更新后的计划，
 * 实现跨迭代的状态传递。
 */
@Slf4j
@Service
public class PlannerService {

    private final PlannerWorkflow plannerWorkflow;

    @Autowired
    public PlannerService(ChatModel chatModel, AfterSalesTools tools) {
        log.info("Building PlannerWorkflow with chatModel={}", chatModel);

        // 1. 构建 PlanAgent — 分析批量工单，生成结构化执行计划（输出 key="plan"）
        PlanAgent planAgent = AgenticServices
                .agentBuilder(PlanAgent.class)
                .chatModel(chatModel)
                .tools(tools)
                .outputKey("plan")
                .build();
        log.info("PlanAgent built: {}", planAgent);

        // 2. 构建 PlannerExecuteAgent — 按计划逐步处理工单（读写同一个 "plan" 状态，实现迭代间累积更新）
        PlannerExecuteAgent executeAgent = AgenticServices
                .agentBuilder(PlannerExecuteAgent.class)
                .chatModel(chatModel)
                .tools(tools)
                .outputKey("plan")
                .build();
        log.info("PlannerExecuteAgent built: {}", executeAgent);

        // 3. 构建 ProgressAgent — 检查进度，决定是否继续
        ProgressAgent progressAgent = AgenticServices
                .agentBuilder(ProgressAgent.class)
                .chatModel(chatModel)
                .outputKey("progressResult")
                .build();
        log.info("ProgressAgent built: {}", progressAgent);

        // 4. 构建 Loop 块：ExecuteAgent ↔ ProgressAgent 循环，直到全部完成
        Predicate<AgenticScope> exitCondition = scope -> {
            String progressResult = scope.readState("progressResult", "");
            boolean allDone = progressResult.contains("ALL_COMPLETED");
            log.info("Loop exit check: progressResult contains ALL_COMPLETED = {}, "
                    + "preview={}", allDone, progressResult.substring(0, Math.min(200, progressResult.length())));
            return allDone;
        };

        UntypedAgent loopBlock = AgenticServices
                .loopBuilder()
                .subAgents(executeAgent, progressAgent)
                .exitCondition(exitCondition)
                .maxIterations(10)
                .outputKey("progressResult")
                .build();
        log.info("LoopBlock built: {}", loopBlock);

        // 5. 串联：PlanAgent → Loop(ExecuteAgent, ProgressAgent)
        this.plannerWorkflow = AgenticServices
                .sequenceBuilder(PlannerWorkflow.class)
                .subAgents(planAgent, loopBlock)
                .outputKey("progressResult")
                .build();
        log.info("PlannerWorkflow built: {}", plannerWorkflow);
    }

    /**
     * 执行 Planner 工作流：规划并逐步处理批量工单
     */
    public String handleBatch(String ticketsJson) {
        log.info("handleBatch: ticketsJson preview={}",
                ticketsJson.substring(0, Math.min(200, ticketsJson.length())));
        return plannerWorkflow.handleBatch(ticketsJson);
    }
}
