package com.example.agentic.hitlsimple.config;

import com.example.agentic.hitlsimple.agent.HitlAgent;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 把「真实大模型」装配成 {@link HitlAgent}。
 *
 * <p>关键点：{@link dev.langchain4j.model.chat.ChatModel} 由 {@code langchain4j-open-ai-spring-boot-starter}
 * 按 {@code application.yml} 里的 {@code langchain4j.open-ai.chat-model.*} 自动配置好
 * （本机指向 apihub.agnes-ai.com/v1，模型 agnes-2.0-flash）。我们只是把它交给
 * {@link AiServices} 包一层，就得到了一个可以 {@code preCheck(...)} / {@code execute(...)} 直接调用的 Agent。</p>
 *
 * <p>教学上这行代码最值得讲：<b>三行就把"大模型"变成了一个有业务语义的方法</b>，
 * 模板里的 {@code {{orderId}}} 等占位符由 {@code @V} 标注的参数自动填充。</p>
 */
@Configuration
public class HitlAgentConfig {

    @Bean
    public HitlAgent hitlAgent(ChatModel chatModel) {
        return AiServices.builder(HitlAgent.class)
                .chatModel(chatModel)
                .build();
    }
}
