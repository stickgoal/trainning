package me.maiz.langchain4jdemo.memory;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface MultiUserAssistant {
    @SystemMessage("你是一个友好的助手")
    String chat(@MemoryId int userId, @UserMessage String message);
}
