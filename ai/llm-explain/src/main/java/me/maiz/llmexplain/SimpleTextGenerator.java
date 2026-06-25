package me.maiz.llmexplain;

import me.maiz.llmexplain.util.MatrixOps;

import java.util.*;

/**
 * ============================================
 * 5. 文本生成 — LLM 如何"创作"文字
 * ============================================
 * <p>
 * 这是最让面试者紧张、但也最重要的环节 — LLM到底是怎么生成文本的？
 * <p>
 * 核心流程:
 * <pre>
 * 输入:"我爱" → Token化 → Embedding → Transformer Block × N →
 * Logits → Softmax → 概率分布 → 采样 → 下一个Token
 * → 重复直到遇到<EOS>或达到最大长度
 * </pre>
 * <p>
 * 面试考点：
 * 1. Autoregressive (自回归): 每次生成一个token，把生成的token加入输入继续生成
 * 2. Temperature: 控制概率分布的"锐利"程度，T↓更确定，T↑更随机
 * 3. Top-K sampling: 只从概率最高的K个token中采样
 * 4. Top-P (nucleus) sampling: 只从累积概率达到P的token中采样
 * 5. Beam Search: 同时保留K个候选序列（生成式任务常用）
 * 6. Greedy decoding: 每次都选概率最高的（效果通常不好，容易重复）
 * <p>
 * 本Demo实现了一个微型"语言模型"，用硬编码的转移概率模拟训练好的LLM。
 */
public class SimpleTextGenerator {

    private final SimpleTokenizer tokenizer;

    // 模拟的"模型" — 学习到的转移概率
    // 在真实的LLM中，这是通过数十亿参数和TB级数据训练出来的
    private final Map<String, Map<String, Double>> transitionProbs;

    // 词表大小
    private final int vocabSize;

    public SimpleTextGenerator(SimpleTokenizer tokenizer) {
        this.tokenizer = tokenizer;
        this.vocabSize = tokenizer.vocabSize();

        // 初始化转移概率（模拟训练好的bigram语言模型）
        // 这是简化：真实LLM的上下文远不止一个词
        this.transitionProbs = new HashMap<>();
        initTransitionProbs();
    }

    /**
     * 初始化转移概率 — 模拟一个"微型语言模型"学到的知识
     * <p>
     * 在真实LLM中，这些概率是通过Transformer处理整个上下文后
     * 由最后的线性层(即LM Head) + Softmax计算得到。
     * <p>
     * 这里为了演示，我们手动定义了一些合理的词接续规律。
     */
    private void initTransitionProbs() {
        // 格式：前一个词 → {下一个词 → 概率}

        // 以"我"开头的句子
        Map<String, Double> meNext = new HashMap<>();
        meNext.put("爱", 0.25);
        meNext.put("学习", 0.25);
        meNext.put("喜欢", 0.20);
        meNext.put("是", 0.15);
        meNext.put("有", 0.10);
        meNext.put("<EOS>", 0.05);
        transitionProbs.put("我", meNext);

        // "爱"后面
        Map<String, Double> loveNext = new HashMap<>();
        loveNext.put("学习", 0.35);
        loveNext.put("你", 0.25);
        loveNext.put("研究", 0.15);
        loveNext.put("工作", 0.10);
        loveNext.put("人工智能", 0.10);
        loveNext.put("<EOS>", 0.05);
        transitionProbs.put("爱", loveNext);

        // "学习"后面
        Map<String, Double> learnNext = new HashMap<>();
        learnNext.put("人工智能", 0.25);
        learnNext.put("机器", 0.20);
        learnNext.put("编程", 0.20);
        learnNext.put("语言", 0.15);
        learnNext.put("科技", 0.10);
        learnNext.put("<EOS>", 0.10);
        transitionProbs.put("学习", learnNext);

        // "人工智能"后面
        Map<String, Double> aiNext = new HashMap<>();
        aiNext.put("是", 0.30);
        aiNext.put("很", 0.20);
        aiNext.put("在", 0.15);
        aiNext.put("和", 0.15);
        aiNext.put("<EOS>", 0.20);
        transitionProbs.put("人工智能", aiNext);

        // "机器"后面
        Map<String, Double> machineNext = new HashMap<>();
        machineNext.put("学习", 0.35);
        machineNext.put("语言", 0.25);
        machineNext.put("人", 0.20);
        machineNext.put("<EOS>", 0.20);
        transitionProbs.put("机器", machineNext);

        // "你"后面
        Map<String, Double> youNext = new HashMap<>();
        youNext.put("爱", 0.20);
        youNext.put("喜欢", 0.20);
        youNext.put("学习", 0.20);
        youNext.put("是", 0.15);
        youNext.put("好", 0.15);
        youNext.put("<EOS>", 0.10);
        transitionProbs.put("你", youNext);

        // "很"后面
        Map<String, Double> veryNext = new HashMap<>();
        veryNext.put("好", 0.40);
        veryNext.put("大", 0.30);
        veryNext.put("高", 0.20);
        veryNext.put("<EOS>", 0.10);
        transitionProbs.put("很", veryNext);

        // "好"后面
        Map<String, Double> goodNext = new HashMap<>();
        goodNext.put("<EOS>", 0.60);
        goodNext.put("的", 0.25);
        goodNext.put("和", 0.15);
        transitionProbs.put("好", goodNext);

        // "喜欢"后面
        Map<String, Double> likeNext = new HashMap<>();
        likeNext.put("学习", 0.25);
        likeNext.put("你", 0.25);
        likeNext.put("研究", 0.20);
        likeNext.put("工作", 0.15);
        likeNext.put("<EOS>", 0.15);
        transitionProbs.put("喜欢", likeNext);

        // "是"后面
        Map<String, Double> isNext = new HashMap<>();
        isNext.put("人工智能", 0.25);
        isNext.put("机器", 0.20);
        isNext.put("中国", 0.20);
        isNext.put("科技", 0.15);
        isNext.put("<EOS>", 0.20);
        transitionProbs.put("是", isNext);
    }

    // ========== 采样策略 ==========

    /**
     * Greedy Decoding (贪心解码): 每次都选概率最高的词
     * <p>
     * 优点：简单、确定性强
     * 缺点：容易生成重复、平淡的内容，没有多样性
     */
    public String greedySample(Map<String, Double> probs) {
        String best = null;
        double maxProb = -1;
        for (Map.Entry<String, Double> entry : probs.entrySet()) {
            if (entry.getValue() > maxProb) {
                maxProb = entry.getValue();
                best = entry.getKey();
            }
        }
        return best;
    }

    /**
     * Temperature Sampling: 带温度的随机采样
     * <p>
     * 公式: softmax(logits / T)
     * - T → 0: 趋近于Greedy，确定性强
     * - T = 1: 保持原始分布
     * - T → ∞: 趋近于均匀分布，完全随机
     * <p>
     * 面试考点：
     * 1. Temperature不是什么新概念，就是对logits做缩放
     * 2. 低温度 = 更聚焦、更确定（适合代码生成）
     * 3. 高温度 = 更多样、更有创意（适合诗歌创作）
     * 4. GPT的默认temperature是0.7~1.0
     */
    public String temperatureSample(Map<String, Double> probs, double temperature) {
        // 将概率转为logits再缩放（模拟实际流程）
        List<Map.Entry<String, Double>> items = new ArrayList<>(probs.entrySet());

        // 把概率取log变成logits
        double[] logits = new double[items.size()];
        for (int i = 0; i < items.size(); i++) {
            // 添加一个小概率让所有词都有机会
            double p = items.get(i).getValue() + 1e-6;
            logits[i] = Math.log(p) / temperature;
            if (Double.isInfinite(logits[i])) logits[i] = -20;
        }

        // softmax变成新概率
        double[] newProbs = MatrixOps.softmax(logits);

        // 按新概率随机采样
        double r = Math.random();
        double cumulative = 0;
        for (int i = 0; i < items.size(); i++) {
            cumulative += newProbs[i];
            if (r <= cumulative) {
                return items.get(i).getKey();
            }
        }
        return items.get(items.size() - 1).getKey();
    }

    /**
     * Top-K Sampling: 只从概率最高的K个词中采样
     * <p>
     * 面试考点：为什么需要Top-K？
     * - 避免从大量低概率词中选到不合适的词
     * - K通常取 40~100
     */
    public String topKSample(Map<String, Double> probs, int k, double temperature) {
        List<Map.Entry<String, Double>> items = new ArrayList<>(probs.entrySet());
        // 按概率降序排序
        items.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        // 取Top-K
        List<Map.Entry<String, Double>> topK = items.subList(0, Math.min(k, items.size()));
        Map<String, Double> topKProbs = new LinkedHashMap<>();
        for (Map.Entry<String, Double> entry : topK) {
            topKProbs.put(entry.getKey(), entry.getValue());
        }

        // 重新归一化后按temperature采样
        // 转换回概率
        double[] logits = new double[topK.size()];
        int idx = 0;
        for (Map.Entry<String, Double> entry : topK) {
            logits[idx++] = Math.log(entry.getValue() + 1e-6) / temperature;
        }
        double[] newProbs = MatrixOps.softmax(logits);

        double r = Math.random();
        double cumulative = 0;
        for (int i = 0; i < topK.size(); i++) {
            cumulative += newProbs[i];
            if (r <= cumulative) {
                return topK.get(i).getKey();
            }
        }
        return topK.get(topK.size() - 1).getKey();
    }

    /**
     * ==============================================
     * 自回归生成 (Autoregressive Generation)
     * ==============================================
     * <p>
     * 这就是ChatGPT聊天的底层原理：
     * 1. 把用户输入拼接到对话历史
     * 2. 预测下一个词
     * 3. 把新词加到序列末尾
     * 4. 重复2-3直到满足停止条件
     * 5. 返回新生成的文本
     * <p>
     * 面试考点：自回归 = "用自己的输出作为下一次的输入"
     */
    public List<String> generate(String startToken, int maxLen, String strategy) {
        System.out.println("\n  ┌─────────────────────────────────────────────────────┐");
        System.out.println("  │  自回归生成  (Autoregressive Generation)              │");
        System.out.println("  │  策略: " + strategy);
        System.out.println("  └─────────────────────────────────────────────────────┘");

        List<String> generated = new ArrayList<>();
        generated.add(startToken);

        System.out.println("\n  初始token: \"" + startToken + "\"");
        System.out.println("  开始逐词生成...\n");

        String current = startToken;
        for (int step = 0; step < maxLen; step++) {
            System.out.println("  Step " + (step + 1) + ": 当前=\"" + current + "\"  → ");

            // 查转移概率（模拟LLM的预测）
            Map<String, Double> probs = transitionProbs.get(current);

            if (probs == null) {
                System.out.println("    未找到转移概率，停止生成");
                break;
            }

            // 打印候选词及其概率
            System.out.println("    候选词及概率（模拟LLM输出的logits→softmax）：");
            List<Map.Entry<String, Double>> sorted = new ArrayList<>(probs.entrySet());
            sorted.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
            for (Map.Entry<String, Double> entry : sorted) {
                String bar = new String(new char[(int) (entry.getValue() * 40)]).replace('\0', '█');
                System.out.printf("      %-8s → %5.1f%% %s%n",
                        entry.getKey(), entry.getValue() * 100, bar);
            }

            // 根据不同策略选择下一个词
            String next;
            switch (strategy) {
                case "greedy":
                    next = greedySample(probs);
                    break;
                case "temperature":
                    next = temperatureSample(probs, 0.8);
                    break;
                case "top-k":
                    next = topKSample(probs, 3, 0.8);
                    break;
                default:
                    next = greedySample(probs);
            }

            System.out.println("    ── 选择: \"" + next + "\"");
            System.out.println();

            if (next.equals("<EOS>")) {
                System.out.println("  遇到<EOS>结束符，生成完成！");
                break;
            }

            generated.add(next);
            current = next;
        }

        System.out.println("  ───────────────────────────────────────────────");
        System.out.print("  生成结果: ");
        for (String token : generated) {
            System.out.print(token);
        }
        System.out.println();
        System.out.println("  ───────────────────────────────────────────────");

        return generated;
    }

    /**
     * 演示不同采样策略的效果
     */
    public void demo() {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║   第5步：文本生成 — LLM 如何「创作」文字            ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
        System.out.println();

        System.out.println("  ▶ 什么是自回归生成 (Autoregressive)？");
        System.out.println("     LLM生成文本就像你写文章——写一个字，看前面写过的，再写下一个。");
        System.out.println("     每一步：输入「已写的词」 → 预测「下一个词」 → 把新词加入输入");
        System.out.println();

        System.out.println("  ▶ 输出层的工作：Logits → Softmax → 概率 → 采样");
        System.out.println("     Transformer Block输出的是向量，通过LM Head(线性层)变成logits");
        System.out.println("     Softmax把logits转成概率分布，然后通过采样策略选词");
        System.out.println();

        // 演示Greedy Decoding
        System.out.println("  ═══════════════════════════════════════════════");
        System.out.println("  实验1: Greedy Decoding（贪心解码）");
        System.out.println("  ═══════════════════════════════════════════════");
        System.out.println("  每次选概率最高的词。稳定但容易重复、平淡。");
        generate("我", 8, "greedy");

        System.out.println();
        System.out.println("  ═══════════════════════════════════════════════");
        System.out.println("  实验2: Temperature Sampling (T=0.8)");
        System.out.println("  ═══════════════════════════════════════════════");
        System.out.println("  引入随机性，每次运行结果可能不同！");
        generate("我", 8, "temperature");

        System.out.println();
        System.out.println("  ═══════════════════════════════════════════════");
        System.out.println("  实验3: Top-K Sampling (K=3, T=0.8)");
        System.out.println("  ═══════════════════════════════════════════════");
        System.out.println("  只从概率最高的3个词中采样，排除\"离谱\"选项");
        generate("我", 8, "top-k");

        // 面试知识点
        System.out.println();
        System.out.println("----------------------------------------");
        System.out.println("💡 面试重点：");
        System.out.println("  1. 自回归生成: 每一步的输出是下一步的输入");
        System.out.println("  2. Softmax将logits转换为概率分布");
        System.out.println("  3. Temperature控制随机性: T↓更确定, T↑更随机");
        System.out.println("  4. Top-K: 只从Top-K个候选采样");
        System.out.println("  5. Top-P (Nucleus): 只从累积概率达P的最小集合采样");
        System.out.println("  6. Greedy: 稳定但效果差，容易重复");
        System.out.println("  7. Beam Search: 同时保留多个候选序列");
        System.out.println("  8. 真实LLM用Top-P + Temperature组合");
        System.out.println("  9. 幻觉(Hallucination) = LLM生成不合理的内容");
        System.out.println("  10. 实际生成参数: temperature=0.7, top_p=0.9, top_k=50");
        System.out.println("----------------------------------------\n");
    }
}
