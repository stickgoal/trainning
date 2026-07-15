package com.example.agentic.humanintheloop.agent;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.V;

/**
 * 退款前置检查 Agent（非 AI，仅做演示用的规则判断）。
 *
 * <p>本类是一个普通的 Java 类，其 {@code @Agent} 静态方法会被
 * {@code AgenticServices.sequenceBuilder(...).subAgents(RefundPreCheckAgent.class, ...)}
 * 经由 {@code createBuiltInAgentExecutor} 自动识别并装配进顺序工作流，
 * 产出写入 AgenticScope 的 {@code preCheckResult}，供人工审批参考。</p>
 */
public class RefundPreCheckAgent {

    private RefundPreCheckAgent() {
    }

    /**
     * 前置检查：模拟规则判断（金额阈值、订单有效性等），不依赖大模型。
     * 返回的文本会作为人工审批的材料。
     */
    @Agent(description = "退款前置检查：校验订单与金额，产出审批材料", outputKey = "preCheckResult")
    public static String preCheck(@V("orderId") String orderId,
                                  @V("reason") String reason,
                                  @V("amount") double amount) {
        boolean largeAmount = amount >= 500;
        return String.format(
                "【前置检查】订单 %s 申请退款 ¥%.2f，原因：%s。%s建议人工审批。",
                orderId, amount, reason, largeAmount ? "金额较大，" : "");
    }
}
