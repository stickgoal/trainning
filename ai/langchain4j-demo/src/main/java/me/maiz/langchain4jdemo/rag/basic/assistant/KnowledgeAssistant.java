package me.maiz.langchain4jdemo.rag.basic.assistant;

import dev.langchain4j.service.UserMessage;

/**
 * AI Service 接口 — 基础 RAG 知识库问答
 *
 * 通过 AiServices.builder() 手动构建，绑定 RetrievalAugmentor，
 * 实现「检索 → 增强 → 生成」的完整 RAG 流程。
 */
public interface KnowledgeAssistant {

    /**
     * 基于知识库内容回答问题
     *
     * @param question 用户问题
     * @return 基于检索结果的回答
     */
    String answer(@UserMessage String question);
}
