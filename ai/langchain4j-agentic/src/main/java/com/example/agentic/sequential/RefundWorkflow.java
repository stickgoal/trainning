package com.example.agentic.sequential;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * 顺序退款审批工作流的顶层接口。
 * 由 sequenceBuilder 构建，聚合 Risk → Finance → Service 三步。
 */
public interface RefundWorkflow {

    @Agent
    String processRefund(
            @V("orderId") String orderId,
            @V("reason") String reason
    );
}
