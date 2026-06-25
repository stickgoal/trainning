package me.maiz.llmexplain;

import me.maiz.llmexplain.util.MatrixOps;

import java.util.Random;

/**
 * ============================================
 * 4. Transformer Block — 堆叠出强大能力
 * ============================================
 * <p>
 * 一个Transformer Block（也叫Decoder Layer）的结构：
 * <pre>
 * 输入 → [LayerNorm → Multi-Head Attention → 残差连接] → [LayerNorm → FFN → 残差连接] → 输出
 * </pre>
 * <p>
 * 面试考点：
 * 1. Pre-LayerNorm vs Post-LayerNorm（GPT用Pre-Norm，更稳定）
 * 2. 残差连接解决深层网络梯度消失问题
 * 3. FFN (Feed-Forward Network) = Linear → GELU → Linear
 * 4. FFN的隐层维度通常是d_model的4倍
 * 5. GPT-3有96层这样的Block
 * 6. 看LLM参数量：d_model × 4 × d_model × 2 (一个FFN的参数量)
 *    × layers × ... 可以估算
 * <p>
 * 本Demo实现一个简化版Transformer Block，展示完整计算流程。
 */
public class SimpleTransformer {

    private final int dModel;
    private final int dFF;       // FFN隐藏层维度 (通常是 d_model * 4)
    private final SimpleAttention attention;
    private final SimpleEmbedding embedding;
    private final SimpleTokenizer tokenizer;

    // LayerNorm 参数 (可学习，这里固定初始化)
    private final double[] gamma1, beta1;  // 第一个LayerNorm
    private final double[] gamma2, beta2;  // 第二个LayerNorm

    // FFN 权重 (Feed-Forward Network)
    private final double[][] W1, W2;   // 两层线性变换
    private final double[] b1, b2;     // 偏置

    // 用于演示的构造方法
    public SimpleTransformer(SimpleTokenizer tokenizer, int dModel) {
        this.tokenizer = tokenizer;
        this.dModel = dModel;
        this.dFF = dModel * 4;  // FFN隐层维度 = 4 × d_model
        this.attention = new SimpleAttention(tokenizer, dModel, 2);
        this.embedding = new SimpleEmbedding(tokenizer, dModel);

        Random r = new Random(123);

        // LayerNorm参数: γ(缩放)和β(偏移)
        this.gamma1 = randomVec(dModel, r);
        this.beta1 = randomVec(dModel, r);
        this.gamma2 = randomVec(dModel, r);
        this.beta2 = randomVec(dModel, r);

        // FFN权重 (两层)
        this.W1 = randomMatrix(dFF, dModel, r);
        this.b1 = randomVec(dFF, r);
        this.W2 = randomMatrix(dModel, dFF, r);
        this.b2 = randomVec(dModel, r);
    }

    private double[] randomVec(int n, Random r) {
        double[] v = new double[n];
        for (int i = 0; i < n; i++) v[i] = (r.nextDouble() - 0.5) * 0.1;
        return v;
    }

    private double[][] randomMatrix(int rows, int cols, Random r) {
        double[][] m = new double[rows][cols];
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                m[i][j] = (r.nextDouble() - 0.5) * 0.1;
        return m;
    }

    /**
     * ========================================
     * 前馈神经网络 (FFN)
     * ========================================
     * <p>
     * 公式: FFN(x) = GELU(x × W1 + b1) × W2 + b2
     * <p>
     * 通俗理解：
     * - Attention层让每个词\"看到\"其他词
     * - FFN层让每个词\"思考\"自己看到的信息
     * - Attention是通信，FFN是计算
     * <p>
     * 面试考点：
     * 1. FFN = 两层的MLP（多层感知机）
     * 2. 隐层扩展4倍再压缩回来 (bottleneck的反向操作)
     * 3. 早期用ReLU，现在用GELU
     * 4. LLM 90%+的参数都在FFN中
     */
    public double[][] feedForward(double[][] input) {
        int seqLen = input.length;
        double[][] output = new double[seqLen][dModel];

        for (int i = 0; i < seqLen; i++) {
            // Step 1: 第一层线性变换 + GELU激活
            double[] hidden = MatrixOps.matVecMul(W1, input[i]);
            hidden = MatrixOps.add(hidden, b1);
            hidden = MatrixOps.gelu(hidden);

            // Step 2: 第二层线性变换
            double[] out = MatrixOps.matVecMul(W2, hidden);
            out = MatrixOps.add(out, b2);
            output[i] = out;
        }
        return output;
    }

    /**
     * ========================================
     * 完整Transformer Block前向传播
     * ========================================
     * <p>
     * 结构: x → LN → MHA → 残差(+) → LN → FFN → 残差(+) → 输出
     */
    public double[][] forward(double[][] input) {
        int seqLen = input.length;
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║    第4步：Transformer Block — 注意力 + 计算 + 连接   ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
        System.out.println();

        // ── 子层1: (Masked) Multi-Head Self-Attention ──
        System.out.println("  ┌──────────────────────────────────────────────────┐");
        System.out.println("  │  子层1: LayerNorm → Multi-Head Attention → Add  │");
        System.out.println("  └──────────────────────────────────────────────────┘");

        // LayerNorm
        double[][] normed1 = new double[seqLen][dModel];
        for (int i = 0; i < seqLen; i++) {
            normed1[i] = MatrixOps.layerNorm(input[i], gamma1, beta1);
        }
        System.out.println("\n  ▶ ① LayerNorm (层归一化): μ→0, σ→1");
        System.out.println("     对每个token的向量做标准化，稳定训练");
        MatrixOps.printMat("      LayerNorm输出", normed1);

        // Multi-Head Attention (带因果遮蔽)
        System.out.println("\n  ▶ ② Masked Multi-Head Attention");
        double[][] attnOutput = attention.multiHeadAttention(normed1, true);

        // 残差连接: output = input + attention_output
        System.out.println("\n  ▶ ③ 残差连接 (Residual Connection): output = input + attention");
        System.out.println("     解决深层网络的梯度消失问题，让信息直接流过");
        double[][] res1 = new double[seqLen][dModel];
        for (int i = 0; i < seqLen; i++) {
            res1[i] = MatrixOps.add(input[i], attnOutput[i]);
        }
        MatrixOps.printMat("      残差输出", res1);

        // ── 子层2: FFN ──
        System.out.println("\n  ┌──────────────────────────────────────────────────┐");
        System.out.println("  │  子层2: LayerNorm → FFN → Add                    │");
        System.out.println("  └──────────────────────────────────────────────────┘");

        // LayerNorm
        double[][] normed2 = new double[seqLen][dModel];
        for (int i = 0; i < seqLen; i++) {
            normed2[i] = MatrixOps.layerNorm(res1[i], gamma2, beta2);
        }
        System.out.println("\n  ▶ ④ LayerNorm");
        MatrixOps.printMat("      LayerNorm输出", normed2);

        // FFN
        System.out.println("\n  ▶ ⑤ FFN (前馈神经网络) — 每个token独立\"思考\"");
        System.out.println("     Attention让词与词通信，FFN让每个词深度学习模式");
        double[][] ffnOutput = feedForward(normed2);

        // 残差连接
        System.out.println("\n  ▶ ⑥ 残差连接: output = res1 + ffn");
        double[][] res2 = new double[seqLen][dModel];
        for (int i = 0; i < seqLen; i++) {
            res2[i] = MatrixOps.add(res1[i], ffnOutput[i]);
        }
        MatrixOps.printMat("      Transformer Block最终输出", res2);

        System.out.println();
        System.out.println("  ───────────────────────────────────────────────");
        System.out.println("  📌 这就是一个Transformer Block的完整流程");
        System.out.println("  GPT-3 堆叠了96层这样的Block");
        System.out.println("  每一层都在做：通信(Attention) → 计算(FFN) → 连接(残差)");
        System.out.println("  ───────────────────────────────────────────────");

        return res2;
    }

    /**
     * 运行完整演示
     */
    public void demo() {
        // 准备输入
        String sentence = "我爱学习";
        int[] ids = tokenizer.encode(sentence);
        double[][] input = embedding.getSequenceEmbedding(ids);

        System.out.println("  输入句子: \"" + sentence + "\"");
        MatrixOps.printMat("  输入Embedding", input);

        // 执行一个完整的Transformer Block
        forward(input);

        // 面试知识点
        System.out.println();
        System.out.println("----------------------------------------");
        System.out.println("💡 面试重点：");
        System.out.println("  1. Transformer Block = Attention + FFN + Residual + LayerNorm");
        System.out.println("  2. 残差连接（Residual）：解决梯度消失，让深层网络可训练");
        System.out.println("  3. LayerNorm（层归一化）：稳定训练过程");
        System.out.println("  4. FFN（前馈网络）：每个词独立学习复杂特征");
        System.out.println("  5. Attention负责\"交流\"，FFN负责\"思考\"");
        System.out.println("  6. GPT-3: 96层, d_model=12288, 总参数量175B");
        System.out.println("  7. 面试手撕代码: 画出Block结构图，能讲清每一层的作用");
        System.out.println("----------------------------------------\n");
    }
}
