package me.maiz.langchain4jdemo.rag.modular.controller;

import lombok.extern.slf4j.Slf4j;
import me.maiz.langchain4jdemo.rag.modular.service.ModularRagService;
import me.maiz.langchain4jdemo.rag.modular.service.RagResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 模块化 RAG — 透明可定制的 RAG 流水线
 *
 * 与基础 RAG 的区别：
 * 1. 显式编排各模块（查询 → 检索 → 聚合 → 生成）
 * 2. 返回各步骤的耗时和统计信息
 * 3. 每个模块可独立定制和替换
 */
@Slf4j
// @RestController
// @RequestMapping("/rag/modular")
public class RagModularController {

    // @Autowired
    private ModularRagService modularRagService;

    /**
     * 模块化 RAG 问答
     *
     * @param question 用户问题
     * @return 包含回答和各步骤统计信息的详细结果
     */
    // @GetMapping("/ask")
    public RagResult ask(@RequestParam String question) {
        log.info("模块化 RAG 问答 - 问题: {}", question);
        return modularRagService.answer(question);
    }
}
