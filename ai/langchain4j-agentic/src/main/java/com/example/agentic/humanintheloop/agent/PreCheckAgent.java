package com.example.agentic.humanintheloop.agent;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * 前置检查 Agent：在人工审批前完成退款材料的准备与初审。
 * 输出写入 AgenticScope 的 "preCheckResult"，供人工审批环节和后续执行 Agent 参考。
 */
public interface PreCheckAgent {

    @UserMessage("""
        你是电商售后前置审核专员。用户发起了一笔大额退款申请，需要你在提交人工审批前完成材料准备。
        
        订单ID: {{orderId}}
        退款原因: {{reason}}
        申请退款金额: {{amount}} 元
        
        请先调用工具查询订单信息和下单用户信息，然后综合评估并输出审批材料。
        
        请返回如下格式：
        订单概况: [订单商品、状态、总价]
        用户画像: [VIP等级、历史订单数、历史退款次数]
        金额核对: [申请金额与订单金额是否一致，是否属于大额退款]
        初审结论: [APPROVE_SUGGESTED / REJECT_SUGGESTED，并给出一句话理由]
        待审批要点: [提醒主管重点关注的风险点]
        """)
    @Agent(name = "PreCheckAgent",
            description = "退款前置检查Agent，准备人工审批所需的材料并给出初审建议",
            outputKey = "preCheckResult")
    String preCheck(@V("orderId") String orderId,
                    @V("reason") String reason,
                    @V("amount") double amount);
}
