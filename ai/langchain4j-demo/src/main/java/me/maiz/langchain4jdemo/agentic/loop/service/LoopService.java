package me.maiz.langchain4jdemo.agentic.loop.service;

import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.model.chat.ChatModel;
import lombok.extern.slf4j.Slf4j;
import me.maiz.langchain4jdemo.agentic.loop.agents.StyleScorer;
import me.maiz.langchain4jdemo.agentic.workflow.agents.StyleEditor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class LoopService {

    private final StyleEditor styleEditor;
    private final StyleScorer styleScorer;

    @Autowired
    public LoopService(ChatModel chatLanguageModel) {
        log.info("StoryWorkflowService: chatLanguageModel={}", chatLanguageModel);


         styleEditor = AgenticServices
                .agentBuilder(StyleEditor.class)
                .chatModel(chatLanguageModel)
                .outputKey("story")
                .build();
        log.info("StoryWorkflowService: styleEditor={}", styleEditor);

         styleScorer = AgenticServices
                .agentBuilder(StyleScorer.class)
                .chatModel(chatLanguageModel)
                .outputKey("story")
                .build();
        log.info("StoryWorkflowService: styleScorer={}", styleScorer);
    }

    public String iterativeRefine(String initialStory, String style, int maxIterations) {
        log.info("iterativeRefine: initialStory={}, style={}, maxIterations={}", initialStory, style, maxIterations);

        String currentStory = initialStory;

        for (int i = 0; i < maxIterations; i++) {

            // 2. 评分
            double score = styleScorer.score(currentStory, style);


            log.info("第{}轮评分：{}", i+1, score);
            // 1. 编辑
            currentStory = styleEditor.editForStyle(currentStory, style);

            // 3. 达标则退出
            if (score >= 0.85) {
                log.info("第{}轮评分：{}，达标，退出迭代", i+1, score);
                break;
            }
        }
        return currentStory;
    }}
