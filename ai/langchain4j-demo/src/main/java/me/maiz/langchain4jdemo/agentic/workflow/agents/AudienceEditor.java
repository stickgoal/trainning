package me.maiz.langchain4jdemo.agentic.workflow.agents;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

// 1. 定义编辑Agent
public interface AudienceEditor {
    @UserMessage("""
            你是一名专业的编辑。
            请对以下故事进行分析并重新编写，使其更符合{{audience}}的目标读者群体。
            故事内容为：“{{story}}”。
            """)
    @Agent(name = "AudienceEditor",
            description = "对故事进行修改，使其更符合特定的受众群体",
            outputKey = "story")
    String editForAudience(@V("story") String story,
                           @V("audience") String audience);
}
