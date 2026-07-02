package com.example.agentic.supervisor.agent;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * 通知执行 Agent：根据处理方案生成最终通知文案并执行。
 * Supervisor 动态调度，通常在方案制定后执行。
 */
public interface NotifyAgent {

    @UserMessage("""
        你是电商售后通知执行专员。请根据处理方案，生成给客户的通知文案并确认执行。
        
        订单ID: {{orderId}}
        
        处理方案:
        {{solutionResult}}
        
        请执行以下操作：
        
        1. 根据处理方案生成正式的客户通知文案
        2. 确认方案的各步骤是否可以执行
        3. 生成执行清单
        
        请返回执行结果，格式如下：
        客户通知文案:
        [正式的通知文案，包含处理方案、时间线、补偿措施等]
        
        执行清单:
        1. [步骤1]
        2. [步骤2]
        3. [步骤3]
        
        执行状态: [COMPLETED/IN_PROGRESS/FAILED]
        备注: [补充说明]
        """)
    @Agent(name = "NotifyAgent",
            description = "通知执行Agent，生成客户通知文案并执行处理方案",
            outputKey = "notifyResult")
    String notify(
        @V("orderId") String orderId,
        @V("solutionResult") String solutionResult
    );
}
