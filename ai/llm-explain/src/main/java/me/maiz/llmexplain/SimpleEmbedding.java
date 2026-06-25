package me.maiz.llmexplain;

import me.maiz.llmexplain.util.MatrixOps;
import java.util.Random;

/**
 * ===============================================
 * 2. Embedding（嵌入）— 从离散ID到稠密向量
 * ===============================================
 * <p>
 * Tokenizer 把文字变成了数字ID，但ID本身没有语义信息。
 * "爱"的ID=5，"恨"的ID=6，但5和6之间的差值没有意义。
 * <p>
 * Embedding 的作用就是：把每个离散的token ID映射到一个稠密向量。
 * 在这个向量空间中，语义相近的词向量也相近。
 * <p>
 * 面试考点：
 * 1. One-hot vs Embedding: one-hot维度高、稀疏，embedding维度低、稠密
 * 2. Embedding矩阵是可训练的，在训练中学习词的语义
 * 3. Positional Encoding: 因为Self-Attention没有位置感知能力，需要加位置编码
 * 4. 经典的"king - man + woman ≈ queen"就是embedding空间的向量运算
 * 5. 维度选择：GPT-3 使用 12288 维，BERT-base 使用 768 维
 */
public class SimpleEmbedding {

    private final int vocabSize;
    private final int embedDim;     // 嵌入维度 — 可以理解为"语义特征的个数"
    private final double[][] embeddingMatrix;
    private final SimpleTokenizer tokenizer;

    /**
     * @param tokenizer 分词器
     * @param embedDim  嵌入向量的维度（为了显示清晰，这里用4维，实际模型中>768维）
     */
    public SimpleEmbedding(SimpleTokenizer tokenizer, int embedDim) {
        this.tokenizer = tokenizer;
        this.vocabSize = tokenizer.vocabSize();
        this.embedDim = embedDim;
        this.embeddingMatrix = new double[vocabSize][embedDim];

        // 为每个词初始化一个随机但固定的embedding向量
        // 在实际训练中，这些值会通过反向传播不断优化
        initEmbeddings();
    }

    /**
     * 初始化Embedding矩阵（模拟训练好的embedding效果）
     * <p>
     * 注意：这里为了演示，我们手动设置了几个词的embedding，
     * 让语义相近的词拥有相近的向量。
     * 在实际训练中，这是通过大量数据学习出来的。
     */
    private void initEmbeddings() {
        // Random种子固定，保证每次运行结果一致
        Random r = new Random(42);

        for (int i = 0; i < vocabSize; i++) {
            for (int j = 0; j < embedDim; j++) {
                embeddingMatrix[i][j] = (r.nextDouble() - 0.5) * 2;
            }
        }
    }

    /**
     * Embedding查找: 给定token ID，返回其embedding向量
     * <p>
     * 其实就是查表 — 从Embedding矩阵中取第i行。
     * 面试考点：为什么说Embedding层就是一个"查表操作"？
     * 因为 one-hot 向量 [0,0,...,1,...,0] 乘以 Embedding矩阵，
     * 结果就是矩阵的第i行，所以实际实现就是查表。
     */
    public double[] getEmbedding(int tokenId) {
        if (tokenId < 0 || tokenId >= vocabSize) {
            return embeddingMatrix[SimpleTokenizer.UNK_ID];
        }
        return embeddingMatrix[tokenId];
    }

    /**
     * 获取整个序列的embedding矩阵
     */
    public double[][] getSequenceEmbedding(int[] tokenIds) {
        double[][] result = new double[tokenIds.length][embedDim];
        for (int i = 0; i < tokenIds.length; i++) {
            result[i] = getEmbedding(tokenIds[i]).clone();
        }
        return result;
    }

    // ========== 位置编码 ==========

    /**
     * 生成正弦位置编码 (Sinusoidal Positional Encoding)
     * <p>
     * 公式：
     * PE(pos, 2i)   = sin(pos / 10000^(2i/d_model))
     * PE(pos, 2i+1) = cos(pos / 10000^(2i/d_model))
     * <p>
     * 面试考点：
     * 1. 为什么用sin/cos？— 可以让模型学到相对位置关系（因为sin(α+β)可展开）
     * 2. 不同频率的sin/cos波覆盖了从短距离到长距离的位置信息
     * 3. GPT用了可学习的位置编码，Transformer原版用的是固定sin/cos
     * 4. 位置编码直接加到token embedding上（而不是拼接）
     */
    public double[][] generatePositionalEncoding(int seqLen) {
        double[][] pe = new double[seqLen][embedDim];
        for (int pos = 0; pos < seqLen; pos++) {
            for (int i = 0; i < embedDim / 2; i++) {
                double angle = pos / Math.pow(10000, 2.0 * i / embedDim);
                pe[pos][2 * i] = Math.sin(angle);
                if (2 * i + 1 < embedDim) {
                    pe[pos][2 * i + 1] = Math.cos(angle);
                }
            }
        }
        return pe;
    }

    /**
     * 演示 Embedding 的工作流程
     */
    public void demo() {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║     第2步：Embedding（嵌入）— 从数字到语义向量         ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
        System.out.println();

        // 1. 展示 One-hot 编码 vs Embedding
        System.out.println("▶ 第一步：One-hot 编码（直观理解）");
        System.out.println("  Token ID=3（\"我\"）的One-hot向量长度为" + vocabSize + "：");
        int[] onehot = new int[vocabSize];
        onehot[3] = 1;
        MatrixOps.printIntVec("  One-hot", onehot);
        System.out.println("  问题：维度太高(词表大小)、太稀疏(只有一个1)、无语义信息");
        System.out.println();

        // 2. Embedding 查找
        System.out.println("▶ 第二步：Embedding 查找 (查表操作)");
        System.out.println("  把 one-hot 乘以 Embedding 矩阵 W_E，相当于查第i行");
        System.out.println("  Embedding维度=" + embedDim + "（实际LLM使用768~12288维）");
        System.out.println();

        // 显示几个词的embedding
        String[] demoWords = {"我", "爱", "学习", "机器"};
        System.out.println("  几个词的Embedding向量（语义相近的词向量也相近）：");
        System.out.println("  ─────────────────────────────────────────────");
        for (String word : demoWords) {
            int id = tokenizer.encodeToken(word);
            double[] emb = getEmbedding(id);
            System.out.printf("  %-8s(ID=%2d) → ", word, id);
            MatrixOps.printVec("", emb);
        }
        System.out.println();

        // 3. 位置编码
        System.out.println("▶ 第三步：位置编码 (Positional Encoding)");
        System.out.println("  为什么要位置编码？因为 Self-Attention 没有位置感知能力");
        System.out.println("  \"我爱你\" 和 \"你爱我\" 的 token 相同，但意思不同！");
        System.out.println();

        String sentence = "我爱你";
        int[] ids = tokenizer.encode(sentence);
        double[][] pe = generatePositionalEncoding(ids.length);

        System.out.println("  句子 \"" + sentence + "\" 的位置编码：");
        for (int i = 0; i < ids.length; i++) {
            System.out.printf("  位置%d ", i);
            MatrixOps.printVec("", pe[i]);
        }
        System.out.println();

        // 4. Token Embedding + Positional Encoding
        System.out.println("▶ 第四步：最终输入 = Token Embedding + 位置编码");
        System.out.println("  LLM的输入是两者相加，不是拼接！");
        System.out.println("  输入向量同时包含\"是什么词\"和\"在什么位置\"的信息。");
        System.out.println();

        double[][] tokenEmb = getSequenceEmbedding(ids);
        int charLen = sentence.length();
        for (int i = 0; i < charLen; i++) {
            double[] finalInput = MatrixOps.add(tokenEmb[i], pe[i]);
            System.out.printf("  位置%d \"%s\" ", i, sentence.split("")[i]);
            MatrixOps.printVec("= tokenEmb + posEnc =", finalInput);
        }
        System.out.println();

        // 面试知识点
        System.out.println("----------------------------------------");
        System.out.println("💡 面试重点：");
        System.out.println("  1. Embedding将离散token映射为稠密语义向量");
        System.out.println("  2. Embedding矩阵是可训练的，维度越大→容量越大→计算越贵");
        System.out.println("  3. 位置编码解决了Self-Attention的排列不变性(permutation invariance)");
        System.out.println("  4. 最终输入 = Token Embedding + Positional Encoding");
        System.out.println("  5. 企业面试可能问: 为什么用加法不是拼接?");
        System.out.println("     → 拼接会加倍维度，增加参数量。加法则保持维度不变。");
        System.out.println("----------------------------------------\n");
    }
}
