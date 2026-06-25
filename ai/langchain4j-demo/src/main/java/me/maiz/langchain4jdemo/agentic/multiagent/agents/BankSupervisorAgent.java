package me.maiz.langchain4jdemo.agentic.multiagent.agents;

import dev.langchain4j.agentic.declarative.ChatModelSupplier;
import dev.langchain4j.agentic.declarative.SupervisorAgent;
import dev.langchain4j.agentic.supervisor.SupervisorContextStrategy;
import dev.langchain4j.agentic.supervisor.SupervisorResponseStrategy;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;

// 2. Supervisor Agent——路由决策
public interface BankSupervisorAgent {

//    @SupervisorAgent(
//            subAgents = {
//                    AccountAgent.class,
//                    TransactionAgent.class,
//                    PaymentAgent.class
//            },
//            responseStrategy = SupervisorResponseStrategy.SUMMARY,
//            contextStrategy = SupervisorContextStrategy.SUMMARIZATION,
//            description = """
//                    你是银行助手的主管。
//                    根据用户问题，判断应该由哪个专业助手处理：
//                    - 账户查询（余额、支付方式）→ AccountAgent
//                    - 交易记录 → TransactionAgent
//                    - 付款请求 → PaymentAgent
//                    请用中文回答，直接调用对应助手。
//                    """
//    )

    String routeAndAnswer(@V("request") String request);

}
