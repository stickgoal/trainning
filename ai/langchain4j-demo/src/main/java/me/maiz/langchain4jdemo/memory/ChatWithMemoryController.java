package me.maiz.langchain4jdemo.memory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatWithMemoryController {
    @Autowired
    private MultiUserAssistant assistant;

    @PostMapping("/chat5")
    public String chat(@RequestParam int userId, @RequestParam String msg) {
        // 每个userId拥有独立的对话记忆
        return assistant.chat(userId, msg);
    }
}

