package me.maiz.langchain4jdemo.rag.advanced.controller;

import lombok.extern.slf4j.Slf4j;
import me.maiz.langchain4jdemo.rag.advanced.assistant.AdvancedKnowledgeAssistant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 进阶 RAG — 查询重写与多路检索演示
 *
 * 比基础 RAG 多了：
 * 1. 查询重写：用 LLM 将模糊问题改写为精确搜索语句
 * 2. 多路检索：从课程库和 FAQ 库分别检索后合并结果
 */
@Slf4j
@RestController
@RequestMapping("/rag/advanced")
public class RagAdvancedController {

    @Autowired
    private AdvancedKnowledgeAssistant advancedKnowledgeAssistant;

    /**
     * 进阶 RAG 问答（查询重写 + 多路检索）
     *
     * @param question 用户问题
     * @return 优化后的回答
     */
    @GetMapping("/ask")
    public String ask(@RequestParam String question) {
        log.info("进阶 RAG 问答 - 问题: {}", question);
        long start = System.currentTimeMillis();
        try {
            String answer = advancedKnowledgeAssistant.answer(question);
            long elapsed = System.currentTimeMillis() - start;
            log.info("回答完成，耗时: {}ms", elapsed);
            return answer;
        } catch (Exception e) {
            log.error("进阶 RAG 问答失败", e);
            return "抱歉，回答时出现错误: " + e.getMessage();
        }
    }
}
