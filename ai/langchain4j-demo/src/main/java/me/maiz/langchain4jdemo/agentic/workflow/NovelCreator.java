package me.maiz.langchain4jdemo.agentic.workflow;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.V;

public interface NovelCreator {
    @Agent
    String createNovel(
            @V("topic") String topic,
            @V("audience") String audience,
            @V("style") String style
    );
}
