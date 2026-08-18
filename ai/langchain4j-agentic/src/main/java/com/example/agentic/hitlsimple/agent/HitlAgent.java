package com.example.agentic.hitlsimple.agent;

import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * 极简 HITL 的「任务执行 Agent」—— 用 LangChain4j 的 {@link dev.langchain4j.service.AiServices}
 * 包装<b>真实的大模型调用</b>。这是货真价实的 Agent 推理，不是 if/else 模拟。
 *
 * <h3>教学要点：职责分离</h3>
 * 本 Agent 只负责「干活」：
 * <ul>
 *   <li>{@link #preCheck} —— 审批前，让 AI 对退款申请做智能初审，产出给人工审批的参考材料；</li>
 *   <li>{@link #execute} —— 审批后，AI 依据「人工决策 + 前面分析材料」执行退款并生成客户通知。</li>
 * </ul>
 * 而「挂起 / 等待人工 / 凭 requestId 恢复」这套 HITL 机制由 {@code SimpleHitlService} 负责。
 * 把「AI 干活」和「人工卡点」拆开，原理最好讲：人工只是在两个 Agent 调用之间插了一脚。
 */
public interface HitlAgent {

    /**
     * 「审批前」步骤：AI 对退款申请做智能初审，产出给人工审批的参考材料。
     * 真实调用大模型，推理内容（原因分类 / 风险初判 / 审批建议）由模型生成。
     */
    @UserMessage("""
        你是电商售后智能审核员。请对这笔退款申请做"审批前"智能分析，为后续人工审批提供专业材料。

        订单ID: {{orderId}}
        退款原因: {{reason}}
        申请金额: {{amount}} 元

        请尽量给出：
        1. 退款原因分类（质量问题 / 主观不满意 / 物流问题 / 其他）
        2. 风险初判（请明确写出"风险等级: 高 / 中 / 低"及依据）
        3. 给人工审批的建议与重点关注点

        只输出分析内容本身，不要寒暄。
        """)
    String preCheck(@V("orderId") String orderId,
                    @V("reason") String reason,
                    @V("amount") double amount);

    /**
     * 「审批后」步骤：AI 依据人工决策 + 前面分析材料，执行退款并生成客户通知。
     * 这里的 {@code decision} 就是人工在 HITL 卡点注入的结论——这就是「人在环」的落点。
     */
    @UserMessage("""
        你是电商售后执行专员。人工审批刚刚完成。

        原始申请：订单ID {{orderId}}，申请金额 {{amount}} 元
        审批前智能分析材料：
        {{preCheckResult}}

        人工审批结论：{{decision}}（APPROVED=同意退款，REJECTED=拒绝退款）
        审批人备注：{{comment}}

        请依据审批结论执行：
        - 若同意：说明已发起退款，给出退款金额，并写一段发给客户的到账通知文案
        - 若拒绝：说明已驳回，并写一段安抚客户的说明文案

        请输出：执行动作 / 退款金额 / 客户通知（三部分即可）。
        """)
    String execute(@V("orderId") String orderId,
                   @V("amount") double amount,
                   @V("preCheckResult") String preCheckResult,
                   @V("decision") String decision,
                   @V("comment") String comment);
}
