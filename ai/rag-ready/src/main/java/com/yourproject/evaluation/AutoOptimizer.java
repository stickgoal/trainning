package com.yourproject.evaluation;

import com.yourproject.config.RAGProperties;
import com.yourproject.evaluation.EvaluationService.EvaluationReport;
import com.yourproject.evaluation.TestDataGenerator.QAPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * 自动优化器
 * 当评估指标未达标时，自动调整参数最多 3 轮
 *
 * 调优方向：
 * 第1轮：分块策略（chunkSize, overlap）
 * 第2轮：检索参数（HNSW efSearch, RRF k）
 * 第3轮：混合查询权重（vector vs BM25）
 */
@Component
public class AutoOptimizer {

    private static final Logger log = LoggerFactory.getLogger(AutoOptimizer.class);

    @Value("${test.max-optimization-rounds}")
    private int maxRounds;

    @Value("${test.evaluation-output-path}")
    private String outputPath;

    private final EvaluationService evaluationService;
    private final RAGProperties ragProperties;

    public AutoOptimizer(EvaluationService evaluationService, RAGProperties ragProperties) {
        this.evaluationService = evaluationService;
        this.ragProperties = ragProperties;
    }

    /**
     * 执行自动调优
     *
     * @param qaPairs    测试问答对
     * @param initReport 初始评估报告
     * @return 调优历史
     */
    public OptimizationHistory optimize(List<QAPair> qaPairs, EvaluationReport initReport) {
        List<OptimizationRound> rounds = new ArrayList<>();
        OptimizationHistory history = new OptimizationHistory(rounds);

        // 记录初始状态
        rounds.add(new OptimizationRound(
                0, "初始基线",
                "无调优",
                ragProperties.getChunkSize(),
                ragProperties.getOverlapTokens(),
                ragProperties.getHnsw().getEfSearch(),
                ragProperties.getRrf().getK(),
                ragProperties.getHybrid().getVectorWeight(),
                ragProperties.getHybrid().getBm25Weight(),
                initReport.recallAt5(),
                initReport.mrr(),
                initReport.ndcgAt5(),
                initReport.answerMatchRate()
        ));

        // 如果已达标，直接返回
        if (initReport.allTargetsMet()) {
            log.info("✅ 初始评估已全部达标，无需调优");
            generateOptimizationHistory(history);
            return history;
        }

        log.info("初始评估未达标，开始自动调优（最多 {} 轮）", maxRounds);

        EvaluationReport currentReport = initReport;

        for (int round = 1; round <= maxRounds; round++) {
            log.info("========== 调优第 {} 轮 ==========", round);

            OptimizationAction action = determineAction(round);
            applyOptimization(action);
            String description = action.description();

            // 重新评估
            currentReport = evaluationService.evaluate(qaPairs);

            rounds.add(new OptimizationRound(
                    round, action.paramName(), description,
                    ragProperties.getChunkSize(),
                    ragProperties.getOverlapTokens(),
                    ragProperties.getHnsw().getEfSearch(),
                    ragProperties.getRrf().getK(),
                    ragProperties.getHybrid().getVectorWeight(),
                    ragProperties.getHybrid().getBm25Weight(),
                    currentReport.recallAt5(),
                    currentReport.mrr(),
                    currentReport.ndcgAt5(),
                    currentReport.answerMatchRate()
            ));

            log.info("第 {} 轮调优后指标: Recall@5={}, MRR={}, NDCG@5={}, Match={}",
                    round,
                    String.format("%.4f", currentReport.recallAt5()),
                    String.format("%.4f", currentReport.mrr()),
                    String.format("%.4f", currentReport.ndcgAt5()),
                    String.format("%.4f", currentReport.answerMatchRate()));

            if (currentReport.allTargetsMet()) {
                log.info("✅ 第 {} 轮调优后全部达标，停止调优", round);
                break;
            }
        }

        if (!currentReport.allTargetsMet()) {
            log.warn("⚠️ {} 轮调优后仍有指标未达标", maxRounds);
        }

        generateOptimizationHistory(history);
        return history;
    }

    /**
     * 根据轮次决定调优方向
     */
    private OptimizationAction determineAction(int round) {
        return switch (round) {
            case 1 -> new OptimizationAction(
                    "分块策略",
                    "chunkSize: 300→400, overlap: 50→80",
                    () -> {
                        ragProperties.setChunkSize(400);
                        ragProperties.setOverlapTokens(80);
                    }
            );
            case 2 -> new OptimizationAction(
                    "检索参数",
                    "HNSW efSearch: 10→50, RRF k: 60→30",
                    () -> {
                        ragProperties.getHnsw().setEfSearch(50);
                        ragProperties.getRrf().setK(30);
                    }
            );
            case 3 -> new OptimizationAction(
                    "混合查询权重",
                    "vectorWeight: 0.7→0.5, bm25Weight: 0.3→0.5",
                    () -> {
                        ragProperties.getHybrid().setVectorWeight(0.5);
                        ragProperties.getHybrid().setBm25Weight(0.5);
                    }
            );
            default -> new OptimizationAction("无", "已达最大轮次", () -> {});
        };
    }

    private void applyOptimization(OptimizationAction action) {
        log.info("调优方向: {} → {}", action.paramName(), action.description());
        action.runnable().run();
    }

    /**
     * 生成调优历史报告
     */
    private void generateOptimizationHistory(OptimizationHistory history) {
        try {
            Path outputDir = Path.of(outputPath);
            Files.createDirectories(outputDir);

            StringBuilder sb = new StringBuilder();
            sb.append("# RAG 系统调优历史报告\n\n");
            sb.append("| 轮次 | 调优方向 | 描述 | chunkSize | overlap | efSearch | RRF_k | vector_w | bm25_w | Recall@5 | MRR | NDCG@5 | Match |\n");
            sb.append("|------|---------|------|-----------|---------|----------|-------|----------|---------|----------|-----|--------|-------|\n");

            for (OptimizationRound round : history.rounds()) {
                sb.append(String.format("| %d | %s | %s | %d | %d | %d | %d | %.2f | %.2f | %.4f | %.4f | %.4f | %.4f |\n",
                        round.round(),
                        round.paramName(),
                        round.description(),
                        round.chunkSize(),
                        round.overlap(),
                        round.efSearch(),
                        round.rrfK(),
                        round.vectorWeight(),
                        round.bm25Weight(),
                        round.recallAt5(),
                        round.mrr(),
                        round.ndcgAt5(),
                        round.answerMatchRate()
                ));
            }

            Files.writeString(outputDir.resolve("optimization-history.md"), sb.toString());
            log.info("调优历史报告已生成: {}/optimization-history.md", outputPath);
        } catch (IOException e) {
            log.error("生成调优历史报告失败", e);
        }
    }

    /**
     * 优化动作
     */
    private record OptimizationAction(String paramName, String description, Runnable runnable) {}

    /**
     * 优化历史
     */
    public record OptimizationHistory(List<OptimizationRound> rounds) {}

    /**
     * 单轮调优记录
     */
    public record OptimizationRound(
            int round,
            String paramName,
            String description,
            int chunkSize,
            int overlap,
            int efSearch,
            int rrfK,
            double vectorWeight,
            double bm25Weight,
            double recallAt5,
            double mrr,
            double ndcgAt5,
            double answerMatchRate
    ) {}
}
