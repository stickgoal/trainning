package me.maiz.langchain4jdemo.agentic.basic;

import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.model.chat.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AgenticConfig {

    @Bean
    public CvGenerator cvGenerator(ChatModel chatModel) {
        return AgenticServices
                .agentBuilder(CvGenerator.class)
                .chatModel(chatModel)
                .outputKey("masterCv")
                .build();
    }
}
