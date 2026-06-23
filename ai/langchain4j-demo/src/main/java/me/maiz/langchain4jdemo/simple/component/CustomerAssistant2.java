package me.maiz.langchain4jdemo.simple.component;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import reactor.core.publisher.Flux;

@AiService
public interface CustomerAssistant2 {
    //从文件加载提示词+流式输出
    @SystemMessage(fromResource="system-prompt.txt")
    Flux<String> ask(@UserMessage String question);

}
