package me.maiz.langchain4jdemo.agentic.workflow.agents;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface StyleEditor {
    @UserMessage("""
            你是一名专业的编辑。
            对以下故事进行分析并重新编写，使其更符合{{style}}风格。
            故事内容为：“{{story}}”。
            """)
    @Agent(name = "StyleEditor",
            description = "对故事进行修改，使其更符合特定的风格",
            outputKey = "story")
    String editForStyle(@V("story") String story,
                        @V("style") String style);
}