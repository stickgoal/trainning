package me.maiz.langchain4jdemo.agentic.multiagent.config;

import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.agentic.supervisor.SupervisorAgent;
import dev.langchain4j.agentic.supervisor.SupervisorContextStrategy;
import dev.langchain4j.agentic.supervisor.SupervisorResponseStrategy;
import dev.langchain4j.model.chat.ChatModel;
import me.maiz.langchain4jdemo.agentic.multiagent.agents.AccountAgent;
import me.maiz.langchain4jdemo.agentic.multiagent.agents.PaymentAgent;
import me.maiz.langchain4jdemo.agentic.multiagent.agents.TransactionAgent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MultiAgentConfig {


    @Bean
    public AccountAgent accountAgent(ChatModel chatModel) {
        return AgenticServices
                .agentBuilder(AccountAgent.class)
                .chatModel(chatModel)
                .build();
    }
    @Bean
    public PaymentAgent paymentAgent(ChatModel chatModel) {
        return AgenticServices
                .agentBuilder(PaymentAgent.class)
                .chatModel(chatModel)
                .build();
    }
    @Bean
    public TransactionAgent transactionAgent(ChatModel chatModel) {
        return AgenticServices
                .agentBuilder(TransactionAgent.class)
                .chatModel(chatModel)
                .build();
    }

    @Bean
    public SupervisorAgent supervisorAgent(ChatModel chatModel, AccountAgent accountAgent, TransactionAgent transactionAgent, PaymentAgent paymentAgent) {
        return AgenticServices
                .supervisorBuilder()
                .subAgents(accountAgent, transactionAgent, paymentAgent)
                .responseStrategy(SupervisorResponseStrategy.SUMMARY)
                .contextGenerationStrategy(SupervisorContextStrategy.SUMMARIZATION)
                .supervisorContext("""
                        你是银行助手的主管。
                        根据用户问题，判断应该由哪个专业助手处理：
                        - 账户查询（余额、支付方式）→ AccountAgent
                        - 交易记录 → TransactionAgent
                        - 付款请求 → PaymentAgent
                        请用中文回答，直接调用对应助手。
                        """)
                .chatModel(chatModel)
                .build();
    }
}
