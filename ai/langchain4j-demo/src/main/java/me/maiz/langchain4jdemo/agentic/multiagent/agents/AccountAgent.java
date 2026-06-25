package me.maiz.langchain4jdemo.agentic.multiagent.agents;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface AccountAgent {
    @Agent(
            description = "你是账户助手，负责查询余额和支付方式，模拟处理账户问题，回复用户处理结果",
            outputKey = "accountResult"
    )
    @SystemMessage("你是账户助手，负责查询余额和支付方式，模拟处理账户问题，回复用户处理结果")
    @UserMessage("查询账户 {{account}} 的余额")
       String handleAccountAgent(@V("account") String account);
}