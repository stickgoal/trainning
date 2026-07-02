package com.example.agentic.humanintheloop.agent;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * 执行 Agent：在人工审批完成后，根据主管的审批结论执行退款并生成处理结果。
 * 该 Agent 依赖 AgenticScope 中的 "managerApproval"，若审批尚未完成会阻塞等待，
 * 从而实现「暂停 → 等待人工 → 恢复执行」的 HumanInTheLoop 语义。
 */
public interface ExecuteAgent {

    @UserMessage("""
        你是电商售后执行专员。人工审批已经完成，请根据主管的审批结论执行退款处理。
        
        订单ID: {{orderId}}
        前置检查材料:
        {{preCheckResult}}
        
        主管审批结论: {{managerApproval}}
        
        执行规则：
        - 若审批结论表示同意(APPROVED/同意/通过)，则执行全额退款并生成到账通知。
        - 若审批结论表示拒绝(REJECTED/拒绝/驳回)，则不予退款并生成安抚说明。
        
        请返回如下格式：
        执行动作: [已退款 / 已驳回]
        退款金额: [实际退款金额，驳回则为0]
        客户通知: [发送给客户的一段通知文案]
        """)
    @Agent(name = "ExecuteAgent",
            description = "退款执行Agent，依据人工审批结论执行退款并生成客户通知",
            outputKey = "executionResult")
    String execute(@V("orderId") String orderId,
                   @V("preCheckResult") String preCheckResult,
                   @V("managerApproval") String managerApproval);
}
