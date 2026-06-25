package me.maiz.langchain4jdemo.agentic.workflow.service;

import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.agentic.UntypedAgent;
import dev.langchain4j.model.chat.ChatModel;
import lombok.extern.slf4j.Slf4j;
import me.maiz.langchain4jdemo.agentic.workflow.NovelCreator;
import me.maiz.langchain4jdemo.agentic.workflow.agents.AudienceEditor;
import me.maiz.langchain4jdemo.agentic.workflow.agents.CreativeWriter;
import me.maiz.langchain4jdemo.agentic.workflow.agents.StyleEditor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class StoryWorkflowService {

    private final NovelCreator storyWorkflow;

    @Autowired
    public StoryWorkflowService(ChatModel chatLanguageModel) {
        log.info("StoryWorkflowService: chatLanguageModel={}", chatLanguageModel);
        // 1. 构建三个子 Agent
        CreativeWriter writer = AgenticServices
                .agentBuilder(CreativeWriter.class)
                .chatModel(chatLanguageModel)
                .outputKey("story")
                .build();
        log.info("StoryWorkflowService: writer={}", writer);
        AudienceEditor audienceEditor = AgenticServices
                .agentBuilder(AudienceEditor.class)
                .chatModel(chatLanguageModel)
                .outputKey("story")
                .build();
        log.info("StoryWorkflowService: audienceEditor={}", audienceEditor);

        StyleEditor styleEditor = AgenticServices
                .agentBuilder(StyleEditor.class)
                .chatModel(chatLanguageModel)
                .outputKey("story")
                .build();
        log.info("StoryWorkflowService: styleEditor={}", styleEditor);

        // 2. 组装顺序工作流
        this.storyWorkflow = AgenticServices
                .sequenceBuilder(NovelCreator.class)
                .subAgents(writer, audienceEditor)
                .outputKey("story")
                .build();
        log.info("StoryWorkflowService: storyWorkflow={}", storyWorkflow);
    }

    /**
     * 执行工作流，返回最终故事
     */
    public String generateStory(String topic, String audience, String style) {
        log.info("generateStory: topic={}, audience={}, style={}", topic, audience, style);
        return storyWorkflow.createNovel(topic, audience, style);
    }
}
