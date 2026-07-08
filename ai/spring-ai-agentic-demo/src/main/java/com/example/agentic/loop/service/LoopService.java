package com.example.agentic.loop.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Service;

/**
 * Loop 迭代优化工作流服务。
 * <p>
 * Spring AI 迁移说明：
 * - LangChain4j 的 loopBuilder + exitCondition → while 循环 + 条件判断
 * - DraftAgent → ReviewAgent → 评分≥8则退出，否则重新起草 → 最多3次迭代
 * <p>
 * 退出条件：ReviewAgent 输出包含 "APPROVED"
 */
@Slf4j
@Service
public class LoopService {

    private static final int MAX_ITERATIONS = 3;

    private final ChatClient chatClient;
    private final ToolCallback[] toolCallbacks;

    public LoopService(ChatClient.Builder chatClientBuilder,
                       ToolCallback[] toolCallbacks) {
        this.chatClient = chatClientBuilder.build();
        this.toolCallbacks = toolCallbacks;
        log.info("LoopService initialized");
    }

    /**
     * 执行迭代优化售后回复文案工作流
     */
    public String generateReply(String orderId, String reason) {
        log.info("generateReply: orderId={}, reason={}", orderId, reason);

        String draftReply = "";
        String reviewFeedback = "";

        for (int iteration = 1; iteration <= MAX_ITERATIONS; iteration++) {
            log.info("=== Loop iteration {}/{} ===", iteration, MAX_ITERATIONS);

            // Step 1: Draft Agent — 起草/修改回复文案
            draftReply = callDraftAgent(orderId, reason, reviewFeedback, iteration);
            log.info("DraftAgent output (iteration {}): {}", iteration, draftReply);

            // Step 2: Review Agent — 评审打分
            String reviewResult = callReviewAgent(draftReply, iteration);
            log.info("ReviewAgent output (iteration {}): {}", iteration, reviewResult);

            // 退出条件：reviewResult 包含 "APPROVED"
            if (reviewResult.contains("APPROVED")) {
                log.info("Loop exited at iteration {} — APPROVED", iteration);
                return "【最终文案（第%d轮通过）】\n%s\n\n【评审结果】\n%s"
                        .formatted(iteration, draftReply, reviewResult);
            }

            // 未通过，将评审反馈传给下一轮 DraftAgent
            reviewFeedback = reviewResult;
        }

        // 达到最大迭代次数仍未通过
        log.warn("Loop reached max iterations ({}) without approval", MAX_ITERATIONS);
        return "【最终文案（已达最大迭代%d次，未完全通过）】\n%s\n\n【最后评审】\n%s"
                .formatted(MAX_ITERATIONS, draftReply, reviewFeedback);
    }

    private String callDraftAgent(String orderId, String reason, String previousFeedback, int iteration) {
        String systemPrompt = """
            你是电商售后文案专员。请根据用户投诉内容起草一封专业、友好的售后回复文案。
            
            要求：
            1. 语气友好专业，体现对用户的关心
            2. 明确说明处理方案和进度
            3. 提供后续联系方式
            4. 文案长度适中，不超过300字
            
            如果提供了上一轮的评审反馈，请根据反馈进行修改完善。
            """;

        StringBuilder userMessage = new StringBuilder();
        userMessage.append("订单ID: %s\n投诉原因: %s\n当前轮次: %d".formatted(orderId, reason, iteration));
        if (previousFeedback != null && !previousFeedback.isEmpty()) {
            userMessage.append("\n\n上一轮评审反馈:\n").append(previousFeedback);
        }

        return chatClient.prompt()
                .system(systemPrompt)
                .user(userMessage.toString())
                .tools(toolCallbacks)
                .call()
                .content();
    }

    private String callReviewAgent(String draftReply, int iteration) {
        String systemPrompt = """
            你是电商售后文案评审专家。请评审以下售后回复文案的质量。
            
            评审维度：
            1. 专业性（是否体现专业素养）
            2. 友好度（语气是否友好温暖）
            3. 完整性（是否涵盖处理方案、进度、联系方式）
            4. 简洁性（是否言简意赅，不啰嗦）
            
            请给出1-10分的综合评分。
            
            如果评分 ≥ 8分，请在结果开头标注 "APPROVED"。
            如果评分 < 8分，请在结果开头标注 "REVISION_NEEDED"，并给出具体改进建议。
            
            返回格式：
            [APPROVED/REVISION_NEEDED]
            综合评分: X/10
            专业性: X/10
            友好度: X/10
            完整性: X/10
            简洁性: X/10
            改进建议: [如有]
            """;

        return chatClient.prompt()
                .system(systemPrompt)
                .user("待评审文案（第%d轮）:\n%s".formatted(iteration, draftReply))
                .call()
                .content();
    }
}
