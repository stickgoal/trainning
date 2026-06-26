package me.maiz.langchain4jdemo.rag.modular.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 模块化 RAG 返回结果
 *
 * 包含回答内容及各步骤的耗时/统计信息，用于展示模块化流程的透明性。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RagResult {
    /** 用户问题 */
    private String question;

    /** AI 回答 */
    private String answer;

    /** 检索到的原始片段数 */
    private int retrievedCount;

    /** 聚合后保留的片段数 */
    private int aggregatedCount;

    /** 检索耗时 (ms) */
    private long retrieveTimeMs;

    /** 聚合耗时 (ms) */
    private long aggregateTimeMs;

    /** 生成耗时 (ms) */
    private long generateTimeMs;

    /** 总耗时 (ms) */
    private long totalTimeMs;

    /** 检索到的片段摘要 */
    private List<String> retrievedSnippets;
}
