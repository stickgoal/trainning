package me.maiz.langchain4jdemo.agentic.workflow;

import dev.langchain4j.agentic.AgenticServices;
import lombok.extern.slf4j.Slf4j;
import me.maiz.langchain4jdemo.agentic.loop.service.LoopService;
import me.maiz.langchain4jdemo.agentic.workflow.service.StoryWorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/novel")
@Slf4j
public class NovelController {

    @Autowired
    private StoryWorkflowService storyWorkflowService;



    @RequestMapping("/create")
    public String createNovel(
            @RequestParam("topic") String topic,
            @RequestParam("audience") String audience,
            @RequestParam("style") String style
    ) {
        log.info("createNovel: topic={}, audience={}, style={}", topic, audience, style);
        return storyWorkflowService.generateStory(topic, audience, style);
    }

    @Autowired
    private LoopService loopService;


    @RequestMapping("/loop")
    public String loop(
            @RequestParam("topic") String topic,
            @RequestParam("audience") String audience,
            @RequestParam("style") String style
    ) {
        String novel = createNovel(topic, audience, style);
        return loopService.iterativeRefine(novel, style, 3);
    }
}
