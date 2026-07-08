package me.maiz.prepare.langchain4j.basic.assistant;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface AIGuide {

    @SystemMessage("""
            你是一个经验丰富的导购人员，根据用户问题，给出相应的建议。
            """)
    public String chat(@UserMessage String message);
}
