package me.maiz.langchain4jdemo.agentic.workflow.agents;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface CreativeWriter {
    @UserMessage("""
            你是一位富有创造力的作家。
            围绕给定的主题生成一段故事草稿。
            主题为 {{topic}}。
            """)
    @Agent(name = "CreativeWriter",
            description = "根据给定的主题生成一个故事",
            outputKey = "story")
    String generateStory(@V("topic") String topic);
}
