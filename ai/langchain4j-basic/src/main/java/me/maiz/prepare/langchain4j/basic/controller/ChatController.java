package me.maiz.prepare.langchain4j.basic.controller;

import dev.langchain4j.model.chat.ChatModel;
import lombok.extern.slf4j.Slf4j;
import me.maiz.prepare.langchain4j.basic.assistant.AICustomerService;
import me.maiz.prepare.langchain4j.basic.assistant.AIGuide;
import me.maiz.prepare.langchain4j.basic.assistant.Priority;
import me.maiz.prepare.langchain4j.basic.assistant.PriorityAnalyzer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class ChatController {
    @Autowired
    private ChatModel chatModel;

    @Autowired
    private AIGuide aiGuide;

    @Autowired
    private PriorityAnalyzer priorityAnalyzer;

    @Autowired
    private AICustomerService aicustomerService;

    @GetMapping("/chat")
    public String chat(String message) {
        return chatModel.chat(message);
    }

    @GetMapping("/aiGuide/chat")
    public String getChat(String message) {
        log.info("Received message: {}", message);
        return aiGuide.chat(message);
    }

    @GetMapping("/aiCustomerService/chat")
    public String getCustomerServiceChat(String message) {
        log.info("Received message for customer service: {}", message);
        return aicustomerService.chat(message);
    }

    @GetMapping("/p/chat")
    public Priority priorityAnalyzer(String message) {
        log.info("Received message for priority analysis: {}", message);
        return priorityAnalyzer.analyzePriority(message);
    }

}
