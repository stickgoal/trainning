package me.maiz.langchain4jdemo.rag.advanced.assistant;

import dev.langchain4j.service.UserMessage;

/**
 * AI Service 接口 — 进阶 RAG（查询重写 + 多路检索）
 */
public interface AdvancedKnowledgeAssistant {

    /**
     * 基于优化检索的知识库问答
     *
     * @param question 用户问题（将被 LLM 重写后检索）
     * @return 基于优化检索的回答
     */
    String answer(@UserMessage String question);
}
