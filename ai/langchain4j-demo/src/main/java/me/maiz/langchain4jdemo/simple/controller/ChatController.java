package me.maiz.langchain4jdemo.simple.controller;

import dev.langchain4j.model.chat.ChatModel;
import me.maiz.langchain4jdemo.simple.common.Priority;
import me.maiz.langchain4jdemo.simple.component.CustomerAssistant;
import me.maiz.langchain4jdemo.simple.component.CustomerAssistant2;
import me.maiz.langchain4jdemo.simple.component.PriorityAnalyzer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class ChatController {

    @Autowired
    private ChatModel chatModel;


    @Autowired
    private CustomerAssistant customerAssistant;

    @Autowired
    private CustomerAssistant2 customerAssistant2;


    @GetMapping("/chat")
    public String chat(@RequestParam String question) {
        return chatModel.chat( question);
    }


    @GetMapping("/chat2")
    public String chat2(@RequestParam String question) {
        return customerAssistant.ask( question);
    }

    @GetMapping(path = "/chat3", produces = "text/event-stream")
    public Flux<String> chat3(@RequestParam String question) {
        return customerAssistant2.ask( question);
    }
    @Autowired
    private PriorityAnalyzer priorityAnalyzer;

    @GetMapping("/chat4")
    public Priority chat4(@RequestParam String question) {
        return priorityAnalyzer.analyzePriority(question);
    }
}
