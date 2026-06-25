package me.maiz.langchain4jdemo.agentic.loop.agents;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface StyleScorer {
    @UserMessage("""
        你是严格的评审。
        请根据与{{style}}风格的契合度，为以下故事打分（0.0~1.0）。
        仅返回分数，不要有其他内容。
        故事：{{story}}
        """)
    @Agent("评估故事与风格的契合度")
    double score(@V("story") String story, @V("style") String style);
}
