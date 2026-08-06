package com.yourproject.evaluation;

import com.yourproject.evaluation.TestDataGenerator.QAPair;
import com.yourproject.service.RagService;
import com.yourproject.service.RagService.RagAnswer;
import com.yourproject.service.RagService.RetrievalInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * 评估服务
 * 计算 Recall@K、MRR、NDCG、AnswerMatchRate 等指标
 * 生成评估报告
 */
@Service
public class EvaluationService {

    private static final Logger log = LoggerFactory.getLogger(EvaluationService.class);

    @Value("${test.evaluation-output-path}")
    private String outputPath;

    @Value("${test.targets.recall-at-5}")
    private double targetRecallAt5;

    @Value("${test.targets.mrr}")
    private double targetMrr;

    @Value("${test.targets.ndcg-at-5}")
    private double targetNdcgAt5;

    @Value("${test.targets.answer-match-rate}")
    private double targetAnswerMatchRate;

    private final RagService ragService;

    public EvaluationService(RagService ragService) {
        this.ragService = ragService;
    }

    /**
     * 执行完整评估
     *
     * @param qaPairs 测试问答对
     * @return 评估报告
     */
    public EvaluationReport evaluate(List<QAPair> qaPairs) {
        log.info("开始评估, 共 {} 个 QA 对", qaPairs.size());

        List<EvalResult> results = new ArrayList<>();
        int recallHits = 0;
        double mrrSum = 0.0;
        double ndcgSum = 0.0;
        int answerMatchCount = 0;

        for (int i = 0; i < qaPairs.size(); i++) {
            QAPair qa = qaPairs.get(i);
            log.info("评估 [{}/{}]: {}", i + 1, qaPairs.size(), qa.question());

            try {
                // 调用 RAG 接口
                RagAnswer answer = ragService.ask(qa.question(), null);

                // 计算检索指标
                boolean recallHit = checkRecall(answer.retrievals(), qa);
                double reciprocalRank = calculateReciprocalRank(answer.retrievals(), qa);
                double ndcg = calculateNDCG(answer.retrievals(), qa);
                boolean answerMatch = checkAnswerMatch(answer.answer(), qa.expectedAnswer());

                if (recallHit) recallHits++;
                mrrSum += reciprocalRank;
                ndcgSum += ndcg;
                if (answerMatch) answerMatchCount++;

                results.add(new EvalResult(
                        qa.question(),
                        qa.expectedAnswer(),
                        answer.answer(),
                        recallHit,
                        reciprocalRank,
                        ndcg,
                        answerMatch,
                        answer.retrievals().size()
                ));
            } catch (Exception e) {
                log.error("评估失败: {}", qa.question(), e);
                results.add(new EvalResult(
                        qa.question(), qa.expectedAnswer(), "ERROR: " + e.getMessage(),
                        false, 0, 0, false, 0
                ));
            }
        }

        // 计算汇总指标
        int total = qaPairs.size();
        double recallAt5 = (double) recallHits / total;
        double mrr = mrrSum / total;
        double ndcgAt5 = ndcgSum / total;
        double answerMatchRate = (double) answerMatchCount / total;

        EvaluationReport report = new EvaluationReport(
                recallAt5, mrr, ndcgAt5, answerMatchRate,
                targetRecallAt5, targetMrr, targetNdcgAt5, targetAnswerMatchRate,
                results
        );

        log.info("评估完成: Recall@5={}, MRR={}, NDCG@5={}, AnswerMatch={}",
                String.format("%.4f", recallAt5), String.format("%.4f", mrr),
                String.format("%.4f", ndcgAt5), String.format("%.4f", answerMatchRate));

        // 生成报告文件
        generateReportFiles(report);

        return report;
    }

    /**
     * 检查 Recall@5：Top-5 检索结果中是否包含正确答案
     */
    private boolean checkRecall(List<RetrievalInfo> retrievals, QAPair qa) {
        if (retrievals == null || retrievals.isEmpty()) return false;

        int topK = Math.min(5, retrievals.size());
        String[] keywords = qa.keywords().split(",");

        for (int i = 0; i < topK; i++) {
            String text = retrievals.get(i).text();
            // 检查是否包含任一关键词
            for (String keyword : keywords) {
                if (text.contains(keyword.trim())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 计算 Reciprocal Rank：正确答案在结果中的倒数排名
     */
    private double calculateReciprocalRank(List<RetrievalInfo> retrievals, QAPair qa) {
        if (retrievals == null || retrievals.isEmpty()) return 0.0;

        String[] keywords = qa.keywords().split(",");
        for (int i = 0; i < retrievals.size(); i++) {
            String text = retrievals.get(i).text();
            for (String keyword : keywords) {
                if (text.contains(keyword.trim())) {
                    return 1.0 / (i + 1); // rank 从 1 开始
                }
            }
        }
        return 0.0;
    }

    /**
     * 计算 NDCG@5
     */
    private double calculateNDCG(List<RetrievalInfo> retrievals, QAPair qa) {
        if (retrievals == null || retrievals.isEmpty()) return 0.0;

        int topK = Math.min(5, retrievals.size());
        String[] keywords = qa.keywords().split(",");

        // 计算 DCG
        double dcg = 0.0;
        for (int i = 0; i < topK; i++) {
            String text = retrievals.get(i).text();
            int relevance = 0;
            for (String keyword : keywords) {
                if (text.contains(keyword.trim())) {
                    relevance = 1;
                    break;
                }
            }
            dcg += relevance / (Math.log(i + 2) / Math.log(2)); // log2(rank+1), rank from 1
        }

        // 计算 IDCG（理想排序的 DCG）
        int totalRelevant = 0;
        for (int i = 0; i < retrievals.size(); i++) {
            String text = retrievals.get(i).text();
            for (String keyword : keywords) {
                if (text.contains(keyword.trim())) {
                    totalRelevant++;
                    break;
                }
            }
        }
        double idcg = 0.0;
        for (int i = 0; i < Math.min(totalRelevant, topK); i++) {
            idcg += 1.0 / (Math.log(i + 2) / Math.log(2));
        }

        return idcg > 0 ? dcg / idcg : 0.0;
    }

    /**
     * 检查生成答案是否匹配预期（简单关键词匹配）
     */
    private boolean checkAnswerMatch(String generated, String expected) {
        if (generated == null || expected == null) return false;

        // 提取预期答案中的关键词
        String[] expectedWords = expected.replaceAll("[，。、（）()0-9%]", " ").split("\\s+");
        Set<String> keyTerms = new HashSet<>();
        for (String word : expectedWords) {
            if (word.length() >= 2) {
                keyTerms.add(word);
            }
        }

        // 检查生成答案是否包含足够的关键词
        int matchCount = 0;
        for (String term : keyTerms) {
            if (generated.contains(term)) {
                matchCount++;
            }
        }

        // 匹配率 >= 50% 视为匹配
        double matchRate = keyTerms.isEmpty() ? 0 : (double) matchCount / keyTerms.size();
        return matchRate >= 0.5;
    }

    /**
     * 生成评估报告文件
     */
    private void generateReportFiles(EvaluationReport report) {
        try {
            Path outputDir = Path.of(outputPath);
            Files.createDirectories(outputDir);

            // 1. evaluation-summary.json
            String summary = report.toJson();
            Files.writeString(outputDir.resolve("evaluation-summary.json"), summary);

            // 2. failure-details.csv
            try (BufferedWriter writer = Files.newBufferedWriter(outputDir.resolve("failure-details.csv"))) {
                writer.write("question,expected,generated,recall_hit,reciprocal_rank,ndcg,answer_match,retrieval_count");
                writer.newLine();
                for (EvalResult r : report.results()) {
                    if (!r.recallHit() || !r.answerMatch()) {
                        writer.write(String.format("%s,%s,%s,%s,%.4f,%.4f,%s,%d",
                                escapeCsv(r.question()),
                                escapeCsv(r.expectedAnswer()),
                                escapeCsv(r.generatedAnswer()),
                                r.recallHit(),
                                r.reciprocalRank(),
                                r.ndcg(),
                                r.answerMatch(),
                                r.retrievalCount()));
                        writer.newLine();
                    }
                }
            }

            log.info("评估报告已生成: {}", outputDir);
        } catch (IOException e) {
            log.error("生成报告文件失败", e);
        }
    }

    private String escapeCsv(String str) {
        if (str == null) return "";
        return "\"" + str.replace("\"", "\"\"") + "\"";
    }

    /**
     * 评估报告
     */
    public record EvaluationReport(
            double recallAt5,
            double mrr,
            double ndcgAt5,
            double answerMatchRate,
            double targetRecallAt5,
            double targetMrr,
            double targetNdcgAt5,
            double targetAnswerMatchRate,
            List<EvalResult> results
    ) {
        public boolean allTargetsMet() {
            return recallAt5 >= targetRecallAt5
                    && mrr >= targetMrr
                    && ndcgAt5 >= targetNdcgAt5
                    && answerMatchRate >= targetAnswerMatchRate;
        }

        public String toJson() {
            return String.format("""
                    {
                      "metrics": {
                        "recall_at_5": %.4f,
                        "mrr": %.4f,
                        "ndcg_at_5": %.4f,
                        "answer_match_rate": %.4f
                      },
                      "targets": {
                        "recall_at_5": %.4f,
                        "mrr": %.4f,
                        "ndcg_at_5": %.4f,
                        "answer_match_rate": %.4f
                      },
                      "all_targets_met": %b,
                      "total_questions": %d
                    }
                    """,
                    recallAt5, mrr, ndcgAt5, answerMatchRate,
                    targetRecallAt5, targetMrr, targetNdcgAt5, targetAnswerMatchRate,
                    allTargetsMet(),
                    results.size()
            );
        }
    }

    /**
     * 单条评估结果
     */
    public record EvalResult(
            String question,
            String expectedAnswer,
            String generatedAnswer,
            boolean recallHit,
            double reciprocalRank,
            double ndcg,
            boolean answerMatch,
            int retrievalCount
    ) {}
}
