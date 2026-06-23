package me.maiz.langchain4jdemo.university;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UniversityController {
    @Autowired
    private UniversityAssistant assistant;

    @PostMapping("/extract")
    public UniversityInfo extract(@RequestParam String text) {
        return assistant.extractUniversityInfo(text);
    }

    @GetMapping("/recommend")
    public String recommend(@RequestParam String province,
                            @RequestParam int score,
                            @RequestParam String interest) {
        return assistant.recommendUniversities(province, score, interest);
    }
}
