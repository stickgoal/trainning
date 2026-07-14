package com.example.agentic.humanintheloop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.agentic.common.tool.AfterSalesTools;
import com.example.agentic.humanintheloop.agent.ExecuteAgent;
import com.example.agentic.humanintheloop.agent.PreCheckAgent;
import com.example.agentic.humanintheloop.entity.ApprovalRequestEntity;
import com.example.agentic.humanintheloop.mapper.ApprovalRequestMapper;
import com.example.agentic.humanintheloop.model.ApprovalResponse;
import com.example.agentic.humanintheloop.model.ApprovalStatus;
import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.model.chat.ChatModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * HumanInTheLoop 人工介入工作流服务（生产化改造版）。
 *
 * <h3>与原实现的关键差异</h3>
 * <ol>
 *   <li><b>状态持久化</b>：审批请求落库到 {@code approval_request} 表（状态机），
 *       取代原实现里 {@code Map<String, Future<String>> runningWorkflows} 这种易失内存状态，
 *       进程重启后可恢复，可审计、可查询。</li>
 *   <li><b>去除阻塞等待</b>：不再用后台线程 + {@code PendingResponse.blockingGet()} 把线程挂起，
 *       也不再在 HTTP 线程里 {@code future.get(120s)} 阻塞。人工审批 = 落库 + 立即返回(202)，
 *       真正执行阶段通过有界线程池异步进行；客户端用 {@code GET /status} 轮询结果。</li>
 *   <li><b>并发安全</b>：审批采用「条件更新」(WHERE status='AWAITING_APPROVAL')，
 *       天然防止两个审批人重复接管控权。</li>
 *   <li><b>可恢复</b>：可选卡滞恢复调度，把长时间停在 EXECUTING 的请求重新入队（默认关闭）。</li>
 * </ol>
 */
@Slf4j
@Service
public class HumanInTheLoopService {

    private final PreCheckAgent preCheckAgent;
    private final ExecuteAgent executeAgent;
    private final ApprovalRequestMapper approvalMapper;
    private final TaskExecutor approvalExecutor;

    @Value("${humanintheloop.recovery.enabled:false}")
    private boolean recoveryEnabled;

    @Autowired
    public HumanInTheLoopService(ChatModel chatModel,
                                 AfterSalesTools tools,
                                 ApprovalRequestMapper approvalMapper,
                                 @Qualifier("humanApprovalTaskExecutor") TaskExecutor approvalExecutor) {
        this.approvalMapper = approvalMapper;
        this.approvalExecutor = approvalExecutor;

        // 前置检查 Agent 与执行 Agent 仍是 LangChain4j Agent，但被当作「两个阶段」分别调用，
        // 中间的「暂停」由数据库状态机承载，不再依赖库内的阻塞 PendingResponse。
        this.preCheckAgent = AgenticServices.agentBuilder(PreCheckAgent.class)
                .chatModel(chatModel)
                .tools(tools)
                .outputKey("preCheckResult")
                .build();
        this.executeAgent = AgenticServices.agentBuilder(ExecuteAgent.class)
                .chatModel(chatModel)
                .tools(tools)
                .outputKey("executionResult")
                .build();
        log.info("HumanInTheLoopService (production mode) initialized");
    }

    /**
     * 提交退款申请：运行前置检查，落库为 {@code AWAITING_APPROVAL}，立即返回。
     * 前置检查是仅数秒的有界 LLM 调用，直接在请求线程完成；真正的「人工等待」不占用任何线程。
     */
    public ApprovalResponse submitRefund(String orderId, String reason, double amount) {
        String requestId = "REQ-" + UUID.randomUUID().toString().substring(0, 8);
        ApprovalRequestEntity entity = ApprovalRequestEntity.builder()
                .requestId(requestId)
                .orderId(orderId)
                .reason(reason)
                .amount(amount)
                .status(ApprovalStatus.PENDING_PRECHECK.code())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        approvalMapper.insert(entity);
        log.info("submitRefund: created requestId={}, orderId={}, amount={}", requestId, orderId, amount);

        try {
            String precheck = preCheckAgent.preCheck(orderId, reason, amount);
            entity.setPrecheckResult(precheck);
            entity.setStatus(ApprovalStatus.AWAITING_APPROVAL.code());
        } catch (Exception e) {
            log.error("submitRefund: precheck failed requestId={}", requestId, e);
            entity.setStatus(ApprovalStatus.FAILED.code());
            entity.setErrorMessage("前置检查失败: " + e.getMessage());
        }
        entity.setUpdatedAt(LocalDateTime.now());
        approvalMapper.updateById(entity);
        return ApprovalResponse.from(entity);
    }

    /**
     * 人工审批：原子地把状态从 {@code AWAITING_APPROVAL} 翻转为 {@code EXECUTING}，
     * 调度异步执行后立即返回（202 语义）。<b>绝不在 HTTP 线程里 {@code future.get()} 阻塞等待。</b>
     *
     * @return 受理后的请求快照（状态应为 EXECUTING）；最终执行结果需通过 {@code GET /status} 轮询。
     */
    public ApprovalResponse approve(String requestId, String decision, String comment, String approver) {
        ApprovalRequestEntity existing = approvalMapper.selectById(requestId);
        if (existing == null) {
            return ApprovalResponse.builder()
                    .requestId(requestId)
                    .status("NOT_FOUND")
                    .message("未找到该审批请求")
                    .build();
        }

        // 条件更新：仅当仍处于 AWAITING_APPROVAL 时才接管控权，避免并发重复审批
        ApprovalRequestEntity patch = ApprovalRequestEntity.builder()
                .decision(decision)
                .decisionComment(comment)
                .approver(approver)
                .status(ApprovalStatus.EXECUTING.code())
                .updatedAt(LocalDateTime.now())
                .build();
        LambdaUpdateWrapper<ApprovalRequestEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ApprovalRequestEntity::getRequestId, requestId)
               .eq(ApprovalRequestEntity::getStatus, ApprovalStatus.AWAITING_APPROVAL.code());
        int rows = approvalMapper.update(patch, wrapper);
        if (rows == 0) {
            ApprovalStatus cur = ApprovalStatus.from(existing.getStatus());
            return ApprovalResponse.builder()
                    .requestId(requestId)
                    .status(existing.getStatus())
                    .message("当前状态为 " + (cur != null ? cur.label() : existing.getStatus()) + "，无法审批")
                    .build();
        }

        // 异步执行真正的退款动作，HTTP 线程立即释放
        approvalExecutor.execute(() -> runExecution(requestId, decision, comment));
        log.info("approve: requestId={} accepted, async execution scheduled", requestId);

        // 读最新快照返回（状态应为 EXECUTING）
        return ApprovalResponse.from(approvalMapper.selectById(requestId));
    }

    /**
     * 异步执行阶段：调用 ExecuteAgent 完成退款并把结果落库。
     * 该阶段运行在专用线程池，并非「人工等待」，故可安全占用线程（有界、有超时预期）。
     */
    private void runExecution(String requestId, String decision, String comment) {
        ApprovalRequestEntity entity = approvalMapper.selectById(requestId);
        if (entity == null) {
            log.warn("runExecution: requestId={} not found", requestId);
            return;
        }
        try {
            String approvalText = decision + (comment == null || comment.isBlank() ? "" : " - " + comment);
            String result = executeAgent.execute(entity.getOrderId(), entity.getPrecheckResult(), approvalText);
            entity.setExecutionResult(result);
            entity.setStatus(isApproved(decision) ? ApprovalStatus.EXECUTED.code() : ApprovalStatus.REJECTED.code());
            entity.setCompletedAt(LocalDateTime.now());
            log.info("runExecution: requestId={} completed status={}", requestId, entity.getStatus());
        } catch (Exception e) {
            log.error("runExecution: requestId={} failed", requestId, e);
            entity.setStatus(ApprovalStatus.FAILED.code());
            entity.setErrorMessage("执行失败: " + e.getMessage());
        } finally {
            entity.setUpdatedAt(LocalDateTime.now());
            approvalMapper.updateById(entity);
        }
    }

    /** 查询当前审批请求快照（供轮询获取最终执行结果）。 */
    public ApprovalResponse getStatus(String requestId) {
        return ApprovalResponse.from(approvalMapper.selectById(requestId));
    }

    /**
     * 卡滞恢复（可选，默认关闭）。把长时间停在 EXECUTING 的请求重新入队执行，
     * 用于进程在 EXECUTING 阶段崩溃后的自愈。阈值 10 分钟需大于单次执行耗时预期。
     */
    @Scheduled(fixedDelay = 60_000)
    public void recoverStuckExecutions() {
        if (!recoveryEnabled) {
            return;
        }
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(10);
        List<ApprovalRequestEntity> stuck = approvalMapper.selectList(
                new LambdaQueryWrapper<ApprovalRequestEntity>()
                        .eq(ApprovalRequestEntity::getStatus, ApprovalStatus.EXECUTING.code())
                        .lt(ApprovalRequestEntity::getUpdatedAt, threshold));
        for (ApprovalRequestEntity e : stuck) {
            log.warn("recoverStuckExecutions: re-running stuck requestId={}", e.getRequestId());
            approvalExecutor.execute(() -> runExecution(e.getRequestId(), e.getDecision(), e.getDecisionComment()));
        }
    }

    private boolean isApproved(String decision) {
        return decision != null && (decision.equalsIgnoreCase("APPROVED")
                || decision.contains("同意") || decision.contains("通过"));
    }
}
