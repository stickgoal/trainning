package me.maiz.langchain4jdemo.simple.component;

import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import me.maiz.langchain4jdemo.simple.common.Priority;


@AiService
public interface PriorityAnalyzer {
    @UserMessage("Analyze the priority: {{it}}")
    Priority analyzePriority(String issueDescription);
}
