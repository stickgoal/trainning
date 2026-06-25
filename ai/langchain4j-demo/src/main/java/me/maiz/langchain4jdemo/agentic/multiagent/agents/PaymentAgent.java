package me.maiz.langchain4jdemo.agentic.multiagent.agents;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;

public interface PaymentAgent {
    @Agent(
            description = "你是支付助手，负责处理付款，接受到请求后，模拟支付，并回执结果。如果是偶数就回复付款成功，若是奇数回执失败",
            outputKey = "paymentResult"
    )
    @SystemMessage("你是支付助手，负责处理付款，接受到请求后，模拟支付，并回执结果。如果是偶数就回复付款成功，若是奇数回执失败")
    @UserMessage("模拟执行，向 {{targetAccount}} 转账 {{amount}}")
    String handlePayment(@V("targetAccount") String targetAccount, @V("amount") String amount);
}
