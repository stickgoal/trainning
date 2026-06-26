package me.maiz.langchain4jdemo.rag.agentic.agent;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * AI Agent 接口 — 招生咨询助手
 *
 * 这是一个 Agentic RAG 的示例：
 * Agent 内部自主决策何时调用 KnowledgeSearchTool 来检索知识库，
 * 而不是每次对话都强制检索。
 */
public interface AdmissionAssistantAgent {

    /**
     * 与招生助手对话
     *
     * @param message 用户消息
     * @return 助手的回复
     */
    @SystemMessage("""
            你是蜗牛学苑的招生咨询助手，你的工作是通过对话帮助潜在学员了解蜗牛学苑。

            行为准则：
            1. 保持友好、专业的语气，用中文回答
            2. 如果你能直接回答的问题（如问候、简单介绍），可以直接回答
            3. 如果遇到不确定的信息（如具体课程价格、校区地址、报名流程等），
               请使用 searchKnowledge 工具搜索知识库获取准确信息
            4. 不要编造任何关于课程、价格、校区等具体信息
            5. 如果工具返回的信息不足以回答用户问题，请诚实地告诉用户
            6. 回答要简洁有条理，适当使用分段和要点
            """)
    String chat(@UserMessage String message);
}
