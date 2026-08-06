package com.yourproject.retrieval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RRF (Reciprocal Rank Fusion) 融合算法
 * 将多个检索结果列表按排名倒数进行融合排序
 *
 * 公式: score(d) = Σ 1/(k + rank_i(d))
 * 其中 k 是平滑常数（默认 60），rank_i(d) 是文档 d 在第 i 个结果列表中的排名（从1开始）
 */
public class RrfFusion {

    private final int k;

    public RrfFusion(int k) {
        this.k = k;
    }

    public RrfFusion() {
        this(60); // 默认 k=60，是论文推荐值
    }

    /**
     * 执行 RRF 融合
     *
     * @param rankedLists 多个有序结果列表（每个列表已经按相关性从高到低排序）
     * @return 融合后的有序结果列表
     */
    public List<FusedResult> fuse(List<List<RetrievalResult>> rankedLists) {
        Map<String, FusedResult> fusedMap = new HashMap<>();

        for (int listIndex = 0; listIndex < rankedLists.size(); listIndex++) {
            List<RetrievalResult> rankedList = rankedLists.get(listIndex);

            for (int rank = 0; rank < rankedList.size(); rank++) {
                RetrievalResult result = rankedList.get(rank);
                String docId = result.docId();

                // RRF 分数: 1/(k + rank+1)  (rank 从0开始，所以 +1)
                double rrfScore = 1.0 / (k + rank + 1);

                FusedResult existing = fusedMap.get(docId);
                if (existing != null) {
                    existing.addScore(rrfScore);
                    existing.addSource(listIndex, rank);
                } else {
                    FusedResult fused = new FusedResult(docId, result.text(), rrfScore);
                    fused.addSource(listIndex, rank);
                    fused.setMetadata(result.metadata());
                    fusedMap.put(docId, fused);
                }
            }
        }

        // 按融合分数降序排序
        List<FusedResult> results = new ArrayList<>(fusedMap.values());
        results.sort((a, b) -> Double.compare(b.fusedScore, a.fusedScore));

        return results;
    }

    /**
     * 融合结果
     */
    public static class FusedResult {
        private final String docId;
        private final String text;
        private double fusedScore;
        private Map<String, String> metadata;
        private final Map<Integer, Integer> sources = new HashMap<>(); // listIndex -> rank

        public FusedResult(String docId, String text, double initialScore) {
            this.docId = docId;
            this.text = text;
            this.fusedScore = initialScore;
        }

        public void addScore(double score) {
            this.fusedScore += score;
        }

        public void addSource(int listIndex, int rank) {
            sources.put(listIndex, rank);
        }

        public void setMetadata(Map<String, String> metadata) {
            this.metadata = metadata;
        }

        public String docId() { return docId; }
        public String text() { return text; }
        public double getFusedScore() { return fusedScore; }
        public Map<String, String> getMetadata() { return metadata; }
        public Map<Integer, Integer> getSources() { return sources; }
    }

    /**
     * 单个检索结果
     */
    public record RetrievalResult(
            String docId,
            String text,
            double score,
            Map<String, String> metadata
    ) {}
}
