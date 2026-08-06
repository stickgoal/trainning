package com.yourproject;

import com.yourproject.evaluation.AutoOptimizer;
import com.yourproject.evaluation.EvaluationService;
import com.yourproject.evaluation.TestDataGenerator;
import com.yourproject.evaluation.TestDataGenerator.QAPair;
import com.yourproject.evaluation.EvaluationService.EvaluationReport;
import com.yourproject.evaluation.AutoOptimizer.OptimizationHistory;
import com.yourproject.service.RagService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RAG 系统全流程评估测试
 *
 * 测试流程：
 * 1. 生成测试数据（多格式文档 + QA 问答对）
 * 2. 初始化 Redis 索引
 * 3. 文档入库（解析 → 分块 → 向量化 → 写入 Redis）
 * 4. 执行评估（Recall@5, MRR, NDCG@5, AnswerMatchRate）
 * 5. 如果未达标，执行自动调优（最多 3 轮）
 * 6. 输出评估报告
 */
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RAGEvaluationTest {

    private static final Logger log = LoggerFactory.getLogger(RAGEvaluationTest.class);

    @Autowired
    private TestDataGenerator testDataGenerator;

    @Autowired
    private RagService ragService;

    @Autowired
    private EvaluationService evaluationService;

    @Autowired
    private AutoOptimizer autoOptimizer;

    private List<QAPair> qaPairs;
    private EvaluationReport initReport;
    private OptimizationHistory optimizationHistory;

    @BeforeAll
    void setup() {
        log.info("========== RAG 评估测试初始化 ==========");

        // 1. 生成测试数据
        qaPairs = testDataGenerator.generateTestDataset();
        assertNotNull(qaPairs, "测试数据生成失败");
        assertTrue(qaPairs.size() >= 20, "QA 问答对数量不足: " + qaPairs.size());
        log.info("测试数据集: {} 个 QA 对", qaPairs.size());

        // 2. 初始化 Redis 索引
        ragService.initAllIndexes();
        log.info("Redis 索引初始化完成");

        // 3. 文档入库
        String testDocsPath = "src/test/resources/test-docs";
        int indexed = ragService.ingestDirectory(testDocsPath, "general");
        log.info("文档入库完成: {} 块", indexed);
        assertTrue(indexed > 0, "文档入库数为 0");
    }

    @Test
    @Order(1)
    @DisplayName("全流程评估：Recall@5 >= 85%")
    void testRecallAt5() {
        initReport = evaluationService.evaluate(qaPairs);
        log.info("初始评估 - Recall@5: {}", String.format("%.4f", initReport.recallAt5()));

        // 如果未达标，触发自动调优
        if (!initReport.allTargetsMet()) {
            optimizationHistory = autoOptimizer.optimize(qaPairs, initReport);
            // 获取调优后的最终报告
            initReport = evaluationService.evaluate(qaPairs);
        }

        assertTrue(initReport.recallAt5() >= 0.85,
                String.format("Recall@5 未达标: %.4f < 0.85", initReport.recallAt5()));
    }

    @Test
    @Order(2)
    @DisplayName("全流程评估：MRR >= 0.8")
    void testMrr() {
        assertNotNull(initReport, "初始评估未执行");
        assertTrue(initReport.mrr() >= 0.8,
                String.format("MRR 未达标: %.4f < 0.8", initReport.mrr()));
    }

    @Test
    @Order(3)
    @DisplayName("全流程评估：NDCG@5 >= 0.75")
    void testNdcgAt5() {
        assertNotNull(initReport, "初始评估未执行");
        assertTrue(initReport.ndcgAt5() >= 0.75,
                String.format("NDCG@5 未达标: %.4f < 0.75", initReport.ndcgAt5()));
    }

    @Test
    @Order(4)
    @DisplayName("全流程评估：AnswerMatchRate >= 70%")
    void testAnswerMatchRate() {
        assertNotNull(initReport, "初始评估未执行");
        assertTrue(initReport.answerMatchRate() >= 0.7,
                String.format("AnswerMatchRate 未达标: %.4f < 0.7", initReport.answerMatchRate()));
    }

    @Test
    @Order(5)
    @DisplayName("自动调优：最多 3 轮，达标即停止")
    void testAutoOptimization() {
        // 初始评估可能已达标，此时 optimizationHistory 为 null 是正常的
        if (optimizationHistory == null) {
            log.info("初始评估已全部达标，未触发自动调优——验证通过");
            assertTrue(initReport != null && initReport.allTargetsMet(),
                    "optimizationHistory 为 null 但指标未达标，逻辑错误");
            return;
        }

        // 验证调优历史
        int rounds = optimizationHistory.rounds().size();
        assertTrue(rounds >= 1, "至少应有 1 轮调优记录（含基线）");
        assertTrue(rounds <= 4, "调优轮次不应超过 maxRounds + 1（基线）");

        log.info("自动调优完成，共 {} 轮", rounds);
        for (var round : optimizationHistory.rounds()) {
            log.info("  轮次 {}: {} → Recall@5={}, MRR={}, Match={}",
                    round.round(), round.description(),
                    String.format("%.4f", round.recallAt5()),
                    String.format("%.4f", round.mrr()),
                    String.format("%.4f", round.answerMatchRate()));
        }
    }

    @Test
    @Order(6)
    @DisplayName("评估报告文件已生成")
    void testReportFilesGenerated() {
        String outputPath = "target/rag-evaluation";
        assertTrue(java.nio.file.Files.exists(java.nio.file.Path.of(outputPath, "evaluation-summary.json")),
                "evaluation-summary.json 未生成");
        assertTrue(java.nio.file.Files.exists(java.nio.file.Path.of(outputPath, "optimization-history.md")),
                "optimization-history.md 未生成");
    }
}
