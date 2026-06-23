package me.maiz.langchain4jdemo.university;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface UniversityAssistant {
    @SystemMessage(fromResource = "university-advisor-system.txt")
    UniversityInfo extractUniversityInfo(@UserMessage String userMessage);

    @SystemMessage("你是一个高考志愿填报专家")
    @UserMessage("根据以下信息推荐3所合适的大学：\n省份：{{province}}\n分数：{{score}}\n兴趣：{{interest}}")
    String recommendUniversities(
            @V("province") String province,
            @V("score") int score,
            @V("interest") String interest
    );
}
