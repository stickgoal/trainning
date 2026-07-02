package me.maiz.langchain4jdemo.rag.agentic.controller;

import lombok.extern.slf4j.Slf4j;
import me.maiz.langchain4jdemo.rag.agentic.agent.AdmissionAssistantAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Agentic RAG — 智能体自主检索
 *
 * Agent 自主判断是否需要检索知识库：
 * - 简单问候 → 不检索，直接回复
 * - 知识类问题 → 调用 searchKnowledge 工具检索
 * - 复杂多步问题 → 可能多次调用工具
 */
@Slf4j
@RestController
@RequestMapping("/rag/agentic")
public class RagAgenticController {

    @Autowired
    private AdmissionAssistantAgent admissionAssistantAgent;

    /**
     * 与招生咨询 Agent 对话
     *
     * @param message 用户消息
     * @return Agent 的回复
     */
    @GetMapping("/chat")
    public String chat(@RequestParam String message) {
        log.info("Agentic RAG 对话 - 消息: {}", message);
        long start = System.currentTimeMillis();
        try {
            String reply = admissionAssistantAgent.chat(message);
            long elapsed = System.currentTimeMillis() - start;
            log.info("Agent 回复完成，耗时: {}ms", elapsed);
            return reply;
        } catch (Exception e) {
            log.error("Agentic RAG 对话失败", e);
            return "抱歉，出现错误: " + e.getMessage();
        }
    }
}
