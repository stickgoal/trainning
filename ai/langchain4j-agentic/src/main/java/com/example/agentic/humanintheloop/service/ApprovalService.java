package com.example.agentic.humanintheloop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.agentic.common.tool.AfterSalesTools;
import com.example.agentic.humanintheloop.HitlRefundWorkflow;
import com.example.agentic.humanintheloop.agent.ExecuteAgent;
import com.example.agentic.humanintheloop.agent.PreCheckAgent;
import com.example.agentic.humanintheloop.agent.RefundApprovalAgent;
import com.example.agentic.humanintheloop.entity.HitlPendingEntity;
import com.example.agentic.humanintheloop.mapper.HitlPendingMapper;
import com.example.agentic.humanintheloop.model.ApprovalResponse;
import com.example.agentic.humanintheloop.model.ApprovalStatus;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.agentic.internal.AgentExecutor;
import dev.langchain4j.agentic.internal.PendingResponse;
import dev.langchain4j.agentic.observability.AgentListener;
import dev.langchain4j.agentic.observability.AgentResponse;
import dev.langchain4j.agentic.scope.AgenticScope;
import dev.langchain4j.agentic.scope.AgenticScopeAccess;
import dev.langchain4j.model.chat.ChatModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * HumanInTheLoop 审批模拟服务（基于 LangChain4j 官方默认机制）。
 *
 * <h3>实现要点</h3>
 * <ol>
 *   <li><b>{@code @HumanInTheLoop} + {@link PendingResponse}</b>：审批方法用注解标记，
 *       返回 {@code PendingResponse}。工作流执行到此处时，下游读取会触发
 *       {@code blockingGet()} <b>阻塞</b>，实现「暂停 → 等待人工 → 恢复」。</li>
 *   <li><b>{@code CompletableFuture} + 阻塞等待</b>：{@link #submitRefund} 创建一个
 *       {@code CompletableFuture}，工作流在后台线程运行并在审批点阻塞；外部审批后
 *       {@link #approve} 通过 {@code completePendingResponse} 恢复执行，并阻塞等待最终结果。</li>
 *   <li><b>序列化/反序列化</b>：暂停时把真实的 {@code PendingResponse} 用 Jackson 序列化成
 *       JSON 落库；{@link #recover} 时反序列化恢复，演示跨进程恢复语义。</li>
 *   <li><b>重启恢复</b>：进程重启后内存中的 AgenticScope 与阻塞线程丢失，但 DB 记录仍在；
 *       {@link #recover} 反序列化 {@code PendingResponse} 取得 {@code responseId}，
 *       重跑工作流并在暂停点注入审批结论，完成中断的审批。</li>
 * </ol>
 *
 * <p><b>注意</b>：本实现刻意保留官方默认的「线程阻塞等待」语义以演示该机制；
 * 其代价是每条在途审批会占用一个线程，生产环境应结合虚拟线程或上一版的非阻塞状态机方案。</p>
 */
@Slf4j
@Service
public class ApprovalService {

    /** HumanInTheLoop 暂停点在 AgenticScope 中的输出键。 */
    public static final String APPROVAL_OUTPUT_KEY = "managerApproval";

    /** PendingResponse.responseId 的前缀，responseId = 前缀 + 业务ID。 */
    public static final String APPROVAL_PREFIX = "approval:";

    /** 等待工作流到达暂停点 / 等待恢复执行结果的最大时长。 */
    private static final long PAUSE_TIMEOUT_MS = 60_000L;
    private static final long RESULT_TIMEOUT_S = 120L;

    private final HitlRefundWorkflow workflow;
    private final HitlPendingMapper pendingMapper;
    private final ExecutorService workflowExecutor;

    /**
     * 专用于 PendingResponse 序列化/反序列化的 ObjectMapper。
     * 关键点：PendingResponse 只有 {@code responseId} 字段可序列化（futureResponse 被 @JsonIgnore），
     * 且其访问器命名为 {@code responseId()}（非 getXxx），Spring 注入的默认 ObjectMapper 按
     * getter/field(PUBLIC) 可见性会把它误判为「无属性」而抛 InvalidDefinitionException。
     * 框架内部 JacksonJsonCodec 用 {@code .visibility(FIELD, ANY)} 解决，这里保持一致。
     */
    private final ObjectMapper pendingObjectMapper = JsonMapper.builder()
            .visibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
            .build();

    /** 业务ID → 工作流结果 future（仅存在于内存，进程重启即丢失）。 */
    private final Map<String, CompletableFuture<String>> running = new ConcurrentHashMap<>();

    /**
     * 业务ID → 暂停信号 future。当 @HumanInTheLoop 产出 PendingResponse 时，
     * 由挂在审批 agent 上的 {@link AgentListener} 在工作流线程上把真实的
     * {@link PendingResponse} 投递过来，调用方 {@code pauseSignal.get(timeout)} 即可，无需轮询。
     * <p>直接携带 PendingResponse（而非 AgenticScope）可避免「listener 在 state 写入前触发」
     * 的竞态：listener 触发时 PendingResponse 已在手，序列化所需数据完整。</p>
     */
    private final Map<String, CompletableFuture<PendingResponse<?>>> pauseSignals = new ConcurrentHashMap<>();

    @Autowired
    public ApprovalService(ChatModel chatModel,
                           AfterSalesTools tools,
                           HitlPendingMapper pendingMapper,
                           @Qualifier("hitlWorkflowExecutor") ExecutorService workflowExecutor) {
        this.pendingMapper = pendingMapper;
        this.workflowExecutor = workflowExecutor;

        // 前置检查 / 执行 为 LLM Agent（AiServices）；审批节点用 @HumanInTheLoop 注解。
        PreCheckAgent preCheckAgent = AgenticServices.agentBuilder(PreCheckAgent.class)
                .chatModel(chatModel).tools(tools).outputKey("preCheckResult").build();
        ExecuteAgent executeAgent = AgenticServices.agentBuilder(ExecuteAgent.class)
                .chatModel(chatModel).tools(tools).outputKey("executionResult").build();

        // 审批节点用 @HumanInTheLoop 注解（自身为 NonAiAgentInstance，无需 chatModel）。
        // 关键点：框架的 inheritedBySubagents 机制在 setParent 时【不会】把根 listener 透传到
        // NonAiAgentInstance —— 其 setParent 仅保存 parent，并不调用 registerInheritedParentListener，
        // 因此 afterAgentInvocation 对非 AI 子 agent 触发时只带它自己的(空) listener。
        // 为可靠捕获 HITL 产出的 PendingResponse，这里显式把暂停监听挂到审批 agent 实例上
        // （registerInheritedParentListener 在 NonAiAgentInstance 中正确实现：只要 inheritedBySubagents()=true
        // 就会把该 listener 组合进审批 agent 自身的 listener；而 AgentInvoker.super.invoke 触发
        // afterAgentInvocation 时用的正是 agent 自身的 listener）。
        AgentExecutor approvalAgent = AgenticServices.createBuiltInAgentExecutor(RefundApprovalAgent.class);
        approvalAgent.registerInheritedParentListener(new AgentListener() {
            @Override
            public boolean inheritedBySubagents() {
                // 仅用于触发 registerInheritedParentListener 的组合逻辑；该 listener 只挂在审批 agent 自身。
                return true;
            }

            @Override
            public void afterAgentInvocation(AgentResponse resp) {
                if (resp.output() instanceof PendingResponse<?> pr) {
                    onApprovalPaused(pr);
                }
            }
        });

        // subAgents 混用「已构建的 LLM Agent 实例」(preCheck/execute) 与「@HumanInTheLoop 实例」(approval)。
        this.workflow = AgenticServices.sequenceBuilder(HitlRefundWorkflow.class)
                .subAgents(preCheckAgent, approvalAgent, executeAgent)
                .outputKey("executionResult")
                .build();
        log.info("ApprovalService initialized: HitlRefundWorkflow built (LLM precheck/execute + @HumanInTheLoop approval, event-driven pause)");
    }

    // =========================================================================
    //  提交退款：创建 CompletableFuture，后台线程阻塞等待人工审批
    // =========================================================================

    /**
     * 提交退款申请：后台运行工作流至审批点暂停，序列化 PendingResponse 落库后返回。
     */
    public ApprovalResponse submitRefund(String orderId, String reason, double amount) {
        log.info("submitRefund: businessId={}, reason={}, amount={}", orderId, reason, amount);

        // 1) future：其完成标志着整条工作流跑完；pauseSignal：到达审批暂停点的信号
        CompletableFuture<String> future = new CompletableFuture<>();
        running.put(orderId, future);
        CompletableFuture<PendingResponse<?>> pauseSignal = new CompletableFuture<>();
        pauseSignals.put(orderId, pauseSignal);

        // 2) 后台线程运行工作流：会在 ExecuteAgent 读取 managerApproval 时阻塞(blockingGet)
        workflowExecutor.submit(() -> {
            try {
                future.complete(workflow.process(orderId, orderId, reason, amount));
            } catch (Throwable t) {
                log.error("submitRefund: workflow failed for businessId={}", orderId, t);
                future.completeExceptionally(t);
                // 工作流在到达暂停点前就失败：解除调用方对 pauseSignal 的等待，避免等满超时
                pauseSignal.completeExceptionally(t);
            }
        });

        // 3) 事件驱动等待到达暂停点：挂在审批 agent 上的 listener 在 PendingResponse 产出后
        //    把真实的 PendingResponse 投递过来，调用方 get(timeout) 即可，无需轮询。
        PendingResponse<?> pending;
        try {
            pending = pauseSignal.get(PAUSE_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            saveOrUpdate(buildEntity(orderId, orderId, reason, amount, responseId(orderId),
                    serialize(new PendingResponse<>(responseId(orderId))),
                    ApprovalStatus.ERROR.code(), null, null, "未在超时内到达审批暂停点"));
            return ApprovalResponse.notFound(orderId, "未在超时内到达审批暂停点");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ApprovalResponse.notFound(orderId, "等待暂停点被中断");
        } catch (ExecutionException e) {
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            saveOrUpdate(buildEntity(orderId, orderId, reason, amount, responseId(orderId),
                    serialize(new PendingResponse<>(responseId(orderId))),
                    ApprovalStatus.ERROR.code(), null, null, "工作流执行异常: " + cause.getMessage()));
            return ApprovalResponse.notFound(orderId, "工作流执行异常: " + cause.getMessage());
        } finally {
            pauseSignals.remove(orderId);
        }

        // 4) listener 已把真实 PendingResponse 直接交给我们，序列化为 JSON 落库
        String serialized = serialize(pending);

        String precheck = readScopeState(orderId, "preCheckResult");
        HitlPendingEntity entity = buildEntity(orderId, orderId, reason, amount,
                pending.responseId(), serialized, ApprovalStatus.PENDING.code(), precheck, null, null);
        saveOrUpdate(entity);

        log.info("submitRefund: paused at approval, businessId={}, responseId={}", orderId, pending.responseId());
        return ApprovalResponse.from(entity, precheck,
                "已暂停等待人工审批，调用 POST /approve?businessId=" + orderId + "&decision=APPROVED 完成审批");
    }

    // =========================================================================
    //  人工审批：完成 PendingResponse，恢复工作流并阻塞等待结果
    // =========================================================================

    /**
     * 注入人工审批结论，恢复工作流执行，并阻塞等待最终结果。
     */
    public ApprovalResponse approve(String orderId, String decision, String comment) {
        log.info("approve: businessId={}, decision={}, comment={}", orderId, decision, comment);

        CompletableFuture<String> future = running.get(orderId);
        AgenticScope scope = ((AgenticScopeAccess) workflow).getAgenticScope(orderId);
        if (future == null || scope == null) {
            return ApprovalResponse.notFound(orderId,
                    "内存中无运行中的会话（可能进程已重启），请改用 POST /recover?businessId=" + orderId + " 恢复");
        }

        String approvalText = decision + (isBlank(comment) ? "" : " - " + comment);
        boolean accepted = scope.completePendingResponse(responseId(orderId), approvalText);
        if (!accepted) {
            return ApprovalResponse.from(pendingMapper.selectById(orderId), null,
                    "该请求已被审批或不处于等待状态");
        }

        // 阻塞等待工作流恢复执行并产出最终结果
        String result;
        try {
            result = future.get(RESULT_TIMEOUT_S, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("approve: wait result failed for businessId={}", orderId, e);
            markStatus(orderId, ApprovalStatus.ERROR.code(), decision, null, "恢复执行失败: " + e.getMessage());
            return ApprovalResponse.from(pendingMapper.selectById(orderId), null, "恢复执行失败: " + e.getMessage());
        } finally {
            running.remove(orderId);
            ((AgenticScopeAccess) workflow).evictAgenticScope(orderId);
        }

        ApprovalStatus finalStatus = isApproved(decision) ? ApprovalStatus.COMPLETED : ApprovalStatus.REJECTED;
        markStatus(orderId, finalStatus.code(), decision, result, null);
        log.info("approve: completed, businessId={}, status={}", orderId, finalStatus.code());
        return ApprovalResponse.from(pendingMapper.selectById(orderId), null, "审批已完成");
    }

    // =========================================================================
    //  重启恢复：反序列化 PendingResponse，重跑并完成中断的审批
    // =========================================================================

    /**
     * 模拟应用重启后，按业务ID恢复并完成中断的审批流程。
     *
     * <p>重启后内存 AgenticScope 与阻塞线程均已丢失，但 DB 中的序列化 PendingResponse 仍在。
     * 这里先【反序列化】取得 responseId，再重跑工作流到暂停点，用该 responseId 完成审批。</p>
     */
    public ApprovalResponse recover(String orderId, String decision, String comment) {
        log.info("recover: businessId={}, decision={}", orderId, decision);
        HitlPendingEntity entity = pendingMapper.selectById(orderId);
        if (entity == null) {
            return ApprovalResponse.notFound(orderId, "未找到该业务的待恢复审批记录");
        }

        // 该记录必须持有序列化的 PendingResponse 才能恢复；ERROR 等记录可能没有。
        if (entity.getSerializedPending() == null) {
            return ApprovalResponse.from(entity, entity.getPrecheckResult(),
                    "该记录无序列化的 PendingResponse（可能之前未完成暂停），无法恢复");
        }

        // ★ 反序列化演示：从 DB 中存的 JSON 还原 PendingResponse
        PendingResponse<?> restored = deserialize(entity.getSerializedPending());
        log.info("recover: deserialized PendingResponse, responseId={}", restored.responseId());

        // 重启后内存 scope 已丢失：重新运行工作流，到达暂停点后注入审批结论
        CompletableFuture<String> future = new CompletableFuture<>();
        running.put(orderId, future);
        CompletableFuture<PendingResponse<?>> pauseSignal = new CompletableFuture<>();
        pauseSignals.put(orderId, pauseSignal);
        workflowExecutor.submit(() -> {
            try {
                future.complete(workflow.process(orderId, orderId, entity.getReason(),
                        entity.getAmount() == null ? 0d : entity.getAmount()));
            } catch (Throwable t) {
                log.error("recover: workflow failed for businessId={}", orderId, t);
                future.completeExceptionally(t);
                pauseSignal.completeExceptionally(t);
            }
        });

        // 事件驱动等待到达暂停点（挂在审批 agent 上的 listener 投递真实 PendingResponse）
        PendingResponse<?> paused;
        try {
            paused = pauseSignal.get(PAUSE_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("recover: wait pause failed for businessId={}", orderId, e);
            markStatus(orderId, ApprovalStatus.ERROR.code(), decision, null, "恢复时未到达审批暂停点: " + e.getMessage());
            return ApprovalResponse.from(pendingMapper.selectById(orderId), null, "恢复失败：未到达审批暂停点");
        } finally {
            pauseSignals.remove(orderId);
        }

        AgenticScope scope = ((AgenticScopeAccess) workflow).getAgenticScope(orderId);
        String approvalText = decision + (isBlank(comment) ? "" : " - " + comment);
        // 用本次重跑产出的 PendingResponse 的 responseId 完成中断的审批（与反序列化所得一致）
        scope.completePendingResponse(paused.responseId(), approvalText);

        String result;
        try {
            result = future.get(RESULT_TIMEOUT_S, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("recover: wait result failed for businessId={}", orderId, e);
            markStatus(orderId, ApprovalStatus.ERROR.code(), decision, null, "恢复执行失败: " + e.getMessage());
            return ApprovalResponse.from(pendingMapper.selectById(orderId), null, "恢复执行失败: " + e.getMessage());
        } finally {
            running.remove(orderId);
            ((AgenticScopeAccess) workflow).evictAgenticScope(orderId);
        }

        ApprovalStatus finalStatus = isApproved(decision) ? ApprovalStatus.RECOVERED : ApprovalStatus.REJECTED;
        markStatus(orderId, finalStatus.code(), decision, result, null);
        log.info("recover: completed, businessId={}, status={}", orderId, finalStatus.code());
        return ApprovalResponse.from(pendingMapper.selectById(orderId), null, "已通过重启恢复完成中断的审批");
    }

    /**
     * 模拟进程重启：清除某业务的内存运行态（AgenticScope + future），但保留 DB 记录。
     */
    public ApprovalResponse simulateRestart(String orderId) {
        running.remove(orderId);
        ((AgenticScopeAccess) workflow).evictAgenticScope(orderId);
        log.warn("simulateRestart: cleared in-memory session for businessId={} (DB record kept)", orderId);
        return ApprovalResponse.from(pendingMapper.selectById(orderId), null,
                "已模拟进程重启：内存会话已清除，DB 记录保留，可调用 /recover 恢复");
    }

    /** 列出所有处于 PENDING（中断/等待中）的审批记录。 */
    public List<HitlPendingEntity> listPending() {
        return pendingMapper.selectList(new LambdaQueryWrapper<HitlPendingEntity>()
                .eq(HitlPendingEntity::getStatus, ApprovalStatus.PENDING.code()));
    }

    public ApprovalResponse getStatus(String orderId) {
        HitlPendingEntity e = pendingMapper.selectById(orderId);
        if (e == null) {
            return ApprovalResponse.notFound(orderId, "未找到该审批记录");
        }
        return ApprovalResponse.from(e, e.getPrecheckResult(), null);
    }

    // =========================================================================
    //  PendingResponse 序列化 / 反序列化（核心演示点）
    // =========================================================================

    /**
     * 序列化 PendingResponse → JSON。
     * <p>注意：PendingResponse 经 Jackson 序列化后只保留 responseId；
     * 其内部的 CompletableFuture 被标为 @JsonIgnore，不会被序列化。</p>
     */
    public String serialize(PendingResponse<?> pending) {
        try {
            String json = pendingObjectMapper.writeValueAsString(pending);
            log.info("[serialize] PendingResponse responseId={} → json={}", pending.responseId(), json);
            return json;
        } catch (Exception ex) {
            throw new RuntimeException("PendingResponse 序列化失败: " + ex.getMessage(), ex);
        }
    }

    /**
     * 反序列化 JSON → PendingResponse。
     * <p>反序列化时会通过 @JsonCreator 构造器重建一个新的、未完成的 CompletableFuture，
     * 使外部系统可"重新连接"并完成该响应。</p>
     */
    public PendingResponse<?> deserialize(String json) {
        try {
            PendingResponse<?> pr = pendingObjectMapper.readValue(json, PendingResponse.class);
            log.info("[deserialize] json={} → PendingResponse responseId={} (新 CompletableFuture 已重建, isDone={})",
                    json, pr.responseId(), pr.isDone());
            return pr;
        } catch (Exception ex) {
            throw new RuntimeException("PendingResponse 反序列化失败: " + ex.getMessage(), ex);
        }
    }

    // =========================================================================
    //  内部工具
    // =========================================================================

    private String responseId(String orderId) {
        return APPROVAL_PREFIX + orderId;
    }

    /**
     * @HumanInTheLoop 产出 PendingResponse 时由挂在审批 agent 上的 listener 回调
     * （运行在工作流线程上）：把真实的 PendingResponse 投递给等待中的 pauseSignal，
     * 调用方即可从忙等轮询改为 get(timeout)。
     */
    private void onApprovalPaused(PendingResponse<?> pending) {
        String rid = pending.responseId();
        String businessId = rid.startsWith(APPROVAL_PREFIX)
                ? rid.substring(APPROVAL_PREFIX.length())
                : rid;
        CompletableFuture<PendingResponse<?>> signal = pauseSignals.get(businessId);
        if (signal != null) {
            log.info("onApprovalPaused: pause reached, businessId={}, responseId={}", businessId, rid);
            signal.complete(pending);
        }
    }

    /** 从运行中的 AgenticScope 读取某个键（用于取前置检查材料等上下文），异常时返回空串。 */
    private String readScopeState(String businessId, String key) {
        try {
            AgenticScope scope = ((AgenticScopeAccess) workflow).getAgenticScope(businessId);
            if (scope == null) {
                return "";
            }
            Object v = scope.readState(key, null);
            return v == null ? "" : v.toString();
        } catch (Exception e) {
            return "";
        }
    }

    private void markStatus(String orderId, String status, String decision, String result, String message) {
        HitlPendingEntity e = pendingMapper.selectById(orderId);
        if (e == null) {
            return;
        }
        e.setStatus(status);
        e.setDecision(decision);
        e.setResult(result);
        e.setUpdatedAt(LocalDateTime.now());
        pendingMapper.updateById(e);
        if (message != null) {
            log.info("markStatus: businessId={}, status={}, note={}", orderId, status, message);
        }
    }

    private HitlPendingEntity buildEntity(String businessId, String orderId, String reason, double amount,
                                          String responseId, String serialized, String status,
                                          String precheckResult, String result, String message) {
        LocalDateTime now = LocalDateTime.now();
        HitlPendingEntity e = HitlPendingEntity.builder()
                .businessId(businessId)
                .orderId(orderId)
                .reason(reason)
                .amount(amount)
                .responseId(responseId)
                .serializedPending(serialized)
                .precheckResult(precheckResult)
                .status(status)
                .result(result)
                .createdAt(now)
                .updatedAt(now)
                .build();
        if (message != null) {
            log.info("buildEntity: businessId={}, status={}, note={}", businessId, status, message);
        }
        return e;
    }

    private void saveOrUpdate(HitlPendingEntity entity) {
        HitlPendingEntity existing = pendingMapper.selectById(entity.getBusinessId());
        if (existing == null) {
            pendingMapper.insert(entity);
        } else {
            entity.setCreatedAt(existing.getCreatedAt());
            pendingMapper.updateById(entity);
        }
    }

    private boolean isApproved(String decision) {
        return decision != null && (decision.equalsIgnoreCase("APPROVED")
                || decision.contains("同意") || decision.contains("通过"));
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
