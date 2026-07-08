package me.maiz.prepare.langchain4j.basic.assistant;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;

/**
 * 结构化输出
 */
@AiService
public interface PriorityAnalyzer {
    @SystemMessage("你是一个客服助理，分析用户问题的严重程度。根据用户问题描述，判断问题的严重程度，并返回 CRITICAL, HIGH, LOW 中的一个。")
    @UserMessage("根据描述分析情况严重程度: {{situation}}")
    Priority analyzePriority(@V("situation")String issueDescription);
}
