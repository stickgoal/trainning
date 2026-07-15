package com.example.agentic.humanintheloop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.agentic.humanintheloop.RefundWorkflow;
import com.example.agentic.humanintheloop.agent.RefundApprovalAgent;
import com.example.agentic.humanintheloop.agent.RefundExecuteAgent;
import com.example.agentic.humanintheloop.agent.RefundPreCheckAgent;
import com.example.agentic.humanintheloop.entity.HitlPendingEntity;
import com.example.agentic.humanintheloop.mapper.HitlPendingMapper;
import com.example.agentic.humanintheloop.model.ApprovalResponse;
import com.example.agentic.humanintheloop.model.ApprovalStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.agentic.internal.PendingResponse;
import dev.langchain4j.agentic.scope.AgenticScope;
import dev.langchain4j.agentic.scope.AgenticScopeAccess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

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

    /** 等待工作流到达暂停点 / 等待恢复执行结果的最大时长。 */
    private static final long PAUSE_TIMEOUT_MS = 60_000L;
    private static final long RESULT_TIMEOUT_S = 120L;

    private final RefundWorkflow workflow;
    private final HitlPendingMapper pendingMapper;
    private final ObjectMapper objectMapper;
    private final ExecutorService workflowExecutor;

    /** 业务ID → 工作流结果 future（仅存在于内存，进程重启即丢失）。 */
    private final Map<String, CompletableFuture<String>> running = new ConcurrentHashMap<>();

    @Autowired
    public ApprovalService(HitlPendingMapper pendingMapper,
                           ObjectMapper objectMapper,
                           @Qualifier("hitlWorkflowExecutor") ExecutorService workflowExecutor) {
        this.pendingMapper = pendingMapper;
        this.objectMapper = objectMapper;
        this.workflowExecutor = workflowExecutor;

        // 用 @HumanInTheLoop 注解类 + 非 AI @Agent 类组装顺序工作流。
        // subAgents 接收 Class，由 createBuiltInAgentExecutor 识别注解并装配，无需 chatModel。
        this.workflow = AgenticServices.sequenceBuilder(RefundWorkflow.class)
                .subAgents(RefundPreCheckAgent.class, RefundApprovalAgent.class, RefundExecuteAgent.class)
                .outputKey("executionResult")
                .build();
        log.info("ApprovalService initialized: RefundWorkflow built with @HumanInTheLoop annotation");
    }

    // =========================================================================
    //  提交退款：创建 CompletableFuture，后台线程阻塞等待人工审批
    // =========================================================================

    /**
     * 提交退款申请：后台运行工作流至审批点暂停，序列化 PendingResponse 落库后返回。
     */
    public ApprovalResponse submitRefund(String orderId, String reason, double amount) {
        log.info("submitRefund: businessId={}, reason={}, amount={}", orderId, reason, amount);

        // 1) 创建 CompletableFuture：其完成标志着整条工作流跑完
        CompletableFuture<String> future = new CompletableFuture<>();
        running.put(orderId, future);

        // 2) 后台线程运行工作流：会在 ExecuteAgent 读取 managerApproval 时阻塞(blockingGet)
        workflowExecutor.submit(() -> {
            try {
                String result = workflow.process(orderId, orderId, reason, amount);
                future.complete(result);
            } catch (Throwable t) {
                log.error("submitRefund: workflow failed for businessId={}", orderId, t);
                future.completeExceptionally(t);
            }
        });

        // 3) 等待工作流到达暂停点（PendingResponse 已写入 AgenticScope）
        AgenticScope scope = awaitPause(orderId, future);
        if (scope == null || future.isDone()) {
            String err = future.isCompletedExceptionally() ? "工作流执行异常" : "未在超时内到达审批暂停点";
            saveOrUpdate(buildEntity(orderId, orderId, reason, amount, responseId(orderId),
                    null, ApprovalStatus.ERROR.code(), null, null, err));
            return ApprovalResponse.notFound(orderId, err);
        }

        // 4) 取出真实的 PendingResponse 并【序列化】落库
        Object stored = scope.state().get(APPROVAL_OUTPUT_KEY);
        PendingResponse<?> pending = (stored instanceof PendingResponse<?> p)
                ? p
                : new PendingResponse<>(responseId(orderId));
        String serialized = serialize(pending);

        String precheck = readQuietly(scope, "preCheckResult");
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

        // ★ 反序列化演示：从 DB 中存的 JSON 还原 PendingResponse
        PendingResponse<?> restored = deserialize(entity.getSerializedPending());
        log.info("recover: deserialized PendingResponse, responseId={}", restored.responseId());

        // 重启后内存 scope 已丢失：重新运行工作流，到达暂停点后注入审批结论
        CompletableFuture<String> future = new CompletableFuture<>();
        running.put(orderId, future);
        workflowExecutor.submit(() -> {
            try {
                future.complete(workflow.process(orderId, orderId, entity.getReason(),
                        entity.getAmount() == null ? 0d : entity.getAmount()));
            } catch (Throwable t) {
                log.error("recover: workflow failed for businessId={}", orderId, t);
                future.completeExceptionally(t);
            }
        });

        AgenticScope scope = awaitPause(orderId, future);
        if (scope == null) {
            markStatus(orderId, ApprovalStatus.ERROR.code(), decision, null, "恢复时未到达审批暂停点");
            return ApprovalResponse.from(pendingMapper.selectById(orderId), null, "恢复失败：未到达审批暂停点");
        }

        String approvalText = decision + (isBlank(comment) ? "" : " - " + comment);
        // 用反序列化得到的 responseId 完成中断的审批
        scope.completePendingResponse(restored.responseId(), approvalText);

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
            String json = objectMapper.writeValueAsString(pending);
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
            PendingResponse<?> pr = objectMapper.readValue(json, PendingResponse.class);
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
        return "approval:" + orderId;
    }

    /** 轮询等待工作流到达审批暂停点：pendingResponseIds 含 responseId，或工作流提前结束。 */
    private AgenticScope awaitPause(String orderId, CompletableFuture<String> future) {
        AgenticScopeAccess access = (AgenticScopeAccess) workflow;
        long deadline = System.currentTimeMillis() + PAUSE_TIMEOUT_MS;
        while (System.currentTimeMillis() < deadline) {
            if (future.isDone()) {
                return access.getAgenticScope(orderId);
            }
            AgenticScope scope = access.getAgenticScope(orderId);
            if (scope != null && scope.pendingResponseIds().contains(responseId(orderId))) {
                return scope;
            }
            sleep(100);
        }
        log.warn("awaitPause: timed out for businessId={}", orderId);
        return access.getAgenticScope(orderId);
    }

    private String readQuietly(AgenticScope scope, String key) {
        try {
            return scope.readState(key, "");
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

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
