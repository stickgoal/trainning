package me.maiz.langchain4jdemo.agentic.multiagent.agents;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;

public interface TransactionAgent {
    @Agent(
            description = "你是交易助手，负责查询转账记录，模拟处理查询，回复一个模拟的结果",
            outputKey = "transactionResult"
    )
    @SystemMessage("你是交易助手，负责查询转账记录，模拟处理查询，回复一个模拟的结果")
    @UserMessage("查询账户 {{targetAccount}} 的转账记录，若提供了时间范围，则查询时间范围 {{startDate}} 到 {{endDate}},如果提供了金额范围则查询金额范围 {{amountMin}} 到 {{amountMax}}内的记录")
    String handleTransaction(@V("targetAccount") String targetAccount,@V("startDate") String startDate,@V("endDate") String endDate,@V("amountMin") String amountMin,@V("amountMax") String amountMax);
}
