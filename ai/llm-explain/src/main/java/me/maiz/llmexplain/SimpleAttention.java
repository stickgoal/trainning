package me.maiz.llmexplain;

import me.maiz.llmexplain.util.MatrixOps;

import java.util.Random;

/**
 * ============================================
 * 3. Attention（注意力机制）— LLM的核心创新
 * ============================================
 * <p>
 * 这是整个Transformer架构中最核心、最重要的概念。
 * 简单说，Attention让模型在处理每个词时，"关注"输入中其他相关词。
 * <p>
 * 核心公式：Attention(Q,K,V) = softmax(Q × K^T / √d_k) × V
 * <p>
 * 用通俗的话理解：
 * - Q（Query）：当前词在"问"什么？
 * - K（Key）：其他词在"说"什么？（与Q的匹配程度）
 * - V（Value）：其他词的实际"内容"是什么？
 * - 结果：根据Q和K的匹配程度，加权汇总所有V
 * <p>
 * 面试考点：
 * 1. Self-Attention：Q、K、V都来自同一个序列，让词之间互相"关注"
 * 2. Cross-Attention：Q来自解码器，K、V来自编码器（Encoder-Decoder架构）
 * 3. Masked Self-Attention：只关注当前位置之前的词（GPT等自回归模型）
 * 4. Multi-Head Attention：多个Attention并行，捕获不同维度的关系
 * 5. Scaled Dot-Product：除以√d_k防止梯度消失
 * 6. 时间复杂度O(n²)：序列越长，计算量指数增长（长文本的瓶颈）
 */
public class SimpleAttention {

    private final int dModel;   // 模型维度（embedding维度）
    private final int dK;       // Q/K 的投影维度
    private final int dV;       // V 的投影维度
    private final int numHeads; // 注意力头数

    // 投影矩阵（模拟训练好的权重）
    private final double[][] WQ, WK, WV, WO;

    private final SimpleTokenizer tokenizer;

    /**
     * @param tokenizer 分词器
     * @param dModel    模型维度（本例用8）
     * @param numHeads  注意力头数（本例用2）
     */
    public SimpleAttention(SimpleTokenizer tokenizer, int dModel, int numHeads) {
        this.tokenizer = tokenizer;
        this.dModel = dModel;
        this.numHeads = numHeads;
        this.dK = dModel / numHeads;  // 每个头的维度
        this.dV = dModel / numHeads;

        Random r = new Random(42);

        // Q, K, V 投影矩阵（训练好的权重）
        // 在真实LLM中，这些矩阵是通过训练学习得到的
        this.WQ = randomMatrix(dModel, dModel, r);
        this.WK = randomMatrix(dModel, dModel, r);
        this.WV = randomMatrix(dModel, dModel, r);
        this.WO = randomMatrix(dModel, dModel, r);
    }

    private double[][] randomMatrix(int rows, int cols, Random r) {
        double[][] m = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                // Xavier初始化：让方差保持稳定，防止梯度爆炸/消失
                double limit = Math.sqrt(6.0 / (rows + cols));
                m[i][j] = (r.nextDouble() * 2 - 1) * limit;
            }
        }
        return m;
    }

    /**
     * ========================================
     * 核心：缩放点积注意力 (Scaled Dot-Product Attention)
     * ========================================
     * <p>
     * 公式：Attention(Q,K,V) = softmax(Q·K^T / √d_k) · V
     * <p>
     * 图解：
     * <pre>
     * 输入序列: [我, 爱, 学习]
     *            ↓
     *  对每个词计算 Q(Query), K(Key), V(Value)
     *            ↓
     *  Q·K^T → 注意力分数矩阵 (谁 关注 谁)
     *            ↓
     *  softmax(分数/√d_k) → 注意力权重 (归一化到0~1)
     *            ↓
     *  权重 × V → 加权汇总 → 输出 (每个词都看到了所有词)
     * </pre>
     */
    public double[][] scaledDotProductAttention(double[][] Q, double[][] K, double[][] V, boolean mask) {
        int seqLen = Q.length;

        // Step 1: Q × K^T = 注意力分数矩阵
        // 形状: (seqLen × dk) × (dk × seqLen) = (seqLen × seqLen)
        double[][] scores = MatrixOps.matMul(Q, MatrixOps.transpose(K));

        System.out.println("    ┌─────────────────────────────────────────┐");
        System.out.println("    │  第一步: Q × K^T = 注意力分数矩阵          │");
        System.out.println("    │  第i行第j列 = 第i个词对第j个词的关注度       │");
        System.out.println("    └─────────────────────────────────────────┘");
        MatrixOps.printMat("    注意力分数 (未缩放)", scores);

        // Step 2: 缩放 (Scale) — 除以 √d_k
        // 为什么？防止维度增大时点积方差变大，softmax进入饱和区
        double scale = Math.sqrt(dK);
        System.out.printf("\n    Step 2: 除以 √d_k = √%d = %.4f  (防止梯度消失)\n", dK, scale);
        for (int i = 0; i < seqLen; i++) {
            for (int j = 0; j < seqLen; j++) {
                scores[i][j] /= scale;
            }
        }
        MatrixOps.printMat("    缩放后的分数", scores);

        // Step 3: (可选) Mask — 因果遮蔽
        // GPT等自回归模型只能看到当前位置之前的词
        if (mask) {
            System.out.println("\n    Step 3: 因果遮蔽 (Causal Mask) — 只关注当前位置及之前的词");
            System.out.println("    因为GPT是从左到右生成的，不能\"看到未来\"的词");
            for (int i = 0; i < seqLen; i++) {
                for (int j = i + 1; j < seqLen; j++) {
                    scores[i][j] = -1e9; // 负无穷，softmax后趋近于0
                }
            }
            MatrixOps.printMat("    Mask后的分数 (右上角被遮罩)", scores);
        }

        // Step 4: Softmax 归一化 — 将每行的分数转为 0~1 的概率分布
        System.out.println("\n    Step 4: Softmax → 注意力权重 (每行之和=1)");
        double[][] weights = new double[seqLen][seqLen];
        for (int i = 0; i < seqLen; i++) {
            weights[i] = MatrixOps.softmax(scores[i]);
        }
        MatrixOps.printMat("    注意力权重", weights);

        // Step 5: 加权汇总 — 权重 × V
        // 每个词的输出 = 所有词V的加权平均，权重由注意力分数决定
        System.out.println("\n    Step 5: 注意力权重 × V = 加权汇总");
        System.out.println("    每个位置的输出 = Σ(该位置对所有位置的关注度 × 对应位置的Value)");
        double[][] output = MatrixOps.matMul(weights, V);
        MatrixOps.printMat("    Attention输出", output);

        return output;
    }

    /**
     * ========================================
     * 完整 Self-Attention 流程
     * ========================================
     */
    public double[][] selfAttention(double[][] input, boolean causalMask) {
        int seqLen = input.length;

        // Step 0: 通过投影矩阵计算 Q, K, V
        System.out.println("\n  ▶ 第0步: 计算 Q(Query), K(Key), V(Value)");
        System.out.println("    用训练好的 WQ, WK, WV 矩阵对输入做线性投影");

        double[][] Q = MatrixOps.matMul(input, WQ);  // Q = X · WQ
        double[][] K = MatrixOps.matMul(input, WK);  // K = X · WK
        double[][] V = MatrixOps.matMul(input, WV);  // V = X · WV

        MatrixOps.printMat("    Q (Query — \"我在找什么\")", Q);
        MatrixOps.printMat("    K (Key — \"我有什么\")", K);
        MatrixOps.printMat("    V (Value — \"我要输出什么\")", V);

        System.out.println("\n  ▶ 第1-5步: 缩放点积注意力");
        double[][] attnOutput = scaledDotProductAttention(Q, K, V, causalMask);

        // Step 6: 输出投影 WO
        System.out.println("\n  ▶ 第6步: 通过 WO 矩阵做输出投影");
        double[][] output = MatrixOps.matMul(attnOutput, WO);
        MatrixOps.printMat("    Self-Attention最终输出", output);

        return output;
    }

    /**
     * ========================================
     * 多头注意力 (简化演示)
     * ========================================
     * <p>
     * 多头注意力的核心思想：每个头可以关注不同的关系
     * - 头1: 关注语法关系（主语-谓语）
     * - 头2: 关注语义关系（实体-属性）
     * - 头3: 关注位置关系（远距离依赖）
     * <p>
     * 面试考点：多头不是多次计算后取平均，而是投影到低维空间
     * 分别计算后拼接(concat)再投影回来。
     * 计算量和单头基本持平（因为维度被切分了）。
     */
    public double[][] multiHeadAttention(double[][] input, boolean causalMask) {
        int seqLen = input.length;

        System.out.println("\n  ┌───────────────────────────────────────────────┐");
        System.out.println("  │  多头注意力 (Multi-Head Attention)              │");
        System.out.println("  │  头数=" + numHeads + ", 每个头维度=" + dK + "       │");
        System.out.println("  │  多头 = 从不同角度理解文本关系                      │");
        System.out.println("  └───────────────────────────────────────────────┘");

        // 先将输入投影到 Q, K, V
        double[][] Q = MatrixOps.matMul(input, WQ);
        double[][] K = MatrixOps.matMul(input, WK);
        double[][] V = MatrixOps.matMul(input, WV);

        // 将Q, K, V拆成 numHeads 个头
        // 原来: Q 是 (seqLen × dModel)
        // 拆后: heads[i] 是 (seqLen × dK)
        double[][][] Qheads = splitHeads(Q);
        double[][][] Kheads = splitHeads(K);
        double[][][] Vheads = splitHeads(V);

        System.out.println("\n  ▶ 将Q/K/V分成 " + numHeads + " 个头，每个头维度=" + dK);

        // 每个头独立计算注意力
        double[][][] headOutputs = new double[numHeads][][];
        for (int h = 0; h < numHeads; h++) {
            System.out.println("\n  ─── 头 " + (h + 1) + " ───");
            headOutputs[h] = scaledDotProductAttention(Qheads[h], Kheads[h], Vheads[h], causalMask);
        }

        // 拼接所有头的输出
        System.out.println("\n  ▶ 拼接所有头的输出 (Concat)");
        double[][] concat = concatHeads(headOutputs);
        MatrixOps.printMat("    拼接后的输出", concat);

        // 通过 WO 投影
        System.out.println("\n  ▶ 通过 WO 做最后投影");
        double[][] output = MatrixOps.matMul(concat, WO);
        MatrixOps.printMat("    多头注意力最终输出", output);

        return output;
    }

    /**
     * 将矩阵按列拆成 numHeads 个子矩阵
     */
    private double[][][] splitHeads(double[][] matrix) {
        double[][][] heads = new double[numHeads][matrix.length][dK];
        for (int h = 0; h < numHeads; h++) {
            for (int i = 0; i < matrix.length; i++) {
                System.arraycopy(matrix[i], h * dK, heads[h][i], 0, dK);
            }
        }
        return heads;
    }

    /**
     * 拼接所有头的输出
     */
    private double[][] concatHeads(double[][][] headOutputs) {
        double[][] result = new double[headOutputs[0].length][dModel];
        for (int i = 0; i < result.length; i++) {
            int col = 0;
            for (int h = 0; h < numHeads; h++) {
                for (int j = 0; j < dK; j++) {
                    result[i][col++] = headOutputs[h][i][j];
                }
            }
        }
        return result;
    }

    /**
     * 完整演示 Self-Attention
     */
    public void demo() {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║  第3步：Attention（注意力机制）— LLM的「灵魂」     ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
        System.out.println();

        int[] ids = tokenizer.encode("我爱学习");
        String sentence = "我爱学习";

        // 用前面做好的embedding模拟输入
        SimpleEmbedding embedding = new SimpleEmbedding(tokenizer, dModel);
        double[][] input = embedding.getSequenceEmbedding(ids);

        System.out.println("  输入句子: \"" + sentence + "\"");
        System.out.println("  输入向量 (d_model=" + dModel + ")：");
        MatrixOps.printMat("  Input", input);
        System.out.println();

        // 单头Self-Attention
        System.out.println("  ───────────────────────────────────────────────");
        System.out.println("  单头 Self-Attention 完整流程");
        System.out.println("  ───────────────────────────────────────────────");
        selfAttention(input, false);

        System.out.println();
        System.out.println("  ───────────────────────────────────────────────");
        System.out.println("  Masked Self-Attention (因果遮蔽)");
        System.out.println("  ───────────────────────────────────────────────");
        System.out.println("  GPT等自回归模型使用Masked Attention，每个词只能看到自己及之前的词。");
        System.out.println("  \"我爱学习\" → 生成\"学\"时，只能看到\"我爱\"，不能看到\"习\"。");
        selfAttention(input, true);

        System.out.println();
        System.out.println("  ───────────────────────────────────────────────");
        System.out.println("  多头注意力 (Multi-Head Attention)");
        System.out.println("  ───────────────────────────────────────────────");
        multiHeadAttention(input, true);

        // 面试知识点
        System.out.println();
        System.out.println("----------------------------------------");
        System.out.println("💡 面试重点：");
        System.out.println("  1. Attention公式: softmax(Q·K^T/√d_k)·V (必考!)");
        System.out.println("  2. Q/K/V都是输入线性投影得到的，不是凭空产生");
        System.out.println("  3. Self-Attention: Q/K/V来自同一序列");
        System.out.println("  4. Cross-Attention: Q来自解码器, K/V来自编码器");
        System.out.println("  5. Scaled (√d_k): 防止softmax进入梯度饱和区");
        System.out.println("  6. Mask: 自回归生成时防止看到未来");
        System.out.println("  7. 多头: 每个头关注不同类型的关系");
        System.out.println("  8. 复杂度O(n²), n=序列长度, 这是长文本处理的瓶颈");
        System.out.println("----------------------------------------\n");
    }
}
