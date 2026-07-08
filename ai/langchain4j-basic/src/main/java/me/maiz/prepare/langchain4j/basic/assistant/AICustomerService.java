package me.maiz.prepare.langchain4j.basic.assistant;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface AICustomerService {
    //从系统文件中读取提示词
    @SystemMessage(fromResource = "system-prompt.txt")
    public String chat(@UserMessage String message);
}
