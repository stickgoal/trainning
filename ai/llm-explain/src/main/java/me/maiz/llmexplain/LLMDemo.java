package me.maiz.llmexplain;

/**
 * LLM原理最小演示 - 从Tokenizer到文本生成，完整演示一次LLM推理流程。
 * 通过5个Step帮助学生理解大语言模型的底层原理。
 */
public class LLMDemo {

    static final int D_MODEL = 8;      // 模型维度 -- 真实LLM: 4096~12288
    static final int NUM_HEADS = 2;     // 注意力头数 -- 真实LLM: 16~96

    public static void main(String[] args) {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║      LLM大语言模型内部原理最小演示                            ║");
        System.out.println("║                                                              ║");
        System.out.println("║  从Tokenizer到文本生成，完整走通一次LLM推理流程                ║");
        System.out.println("║  用Java代码 + 中文注释，帮助理解LLM底层原理                    ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("  LLM 推理完整流程：");
        System.out.println();
        System.out.println("   输入文字 -> 1.Tokenizer(分词) -> 2.Embedding(向量化)");
        System.out.println("       -> 3.Attention(注意力) -> 4.Transformer(堆叠)");
        System.out.println("       -> 5.Softmax(概率) -> 采样 -> 输出文字");
        System.out.println();
        System.out.println("  超参数：d_model=" + D_MODEL + ", num_heads=" + NUM_HEADS);
        System.out.println("  (真实GPT-3: d_model=12288, num_heads=96, layers=96)");
        System.out.println();

        // Step 1: Tokenizer -- 文字 -> 数字ID
        SimpleTokenizer tokenizer = new SimpleTokenizer();
        tokenizer.demo();

        waitForUser();

        // Step 2: Embedding -- 数字ID -> 语义向量
        SimpleEmbedding embedding = new SimpleEmbedding(tokenizer, D_MODEL);
        embedding.demo();

        waitForUser();

        // Step 3: Attention -- 注意力机制（LLM的核心）
        SimpleAttention attention = new SimpleAttention(tokenizer, D_MODEL, NUM_HEADS);
        attention.demo();

        waitForUser();

        // Step 4: Transformer Block -- 把组件装在一起
        SimpleTransformer transformer = new SimpleTransformer(tokenizer, D_MODEL);
        transformer.demo();

        waitForUser();

        // Step 5: Text Generation -- 自回归生成
        SimpleTextGenerator generator = new SimpleTextGenerator(tokenizer);
        generator.demo();

        // 总结
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║  面试知识体系总结                                            ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("  +-----------------------------------------------------+");
        System.out.println("  | Layer (层)           | 作用              | 参数量级  |");
        System.out.println("  +-----------------------------------------------------+");
        System.out.println("  | Token Embedding      | 文字->向量        | vocab*d   |");
        System.out.println("  | Positional Encoding  | 位置信息          | 固定公式   |");
        System.out.println("  | Multi-Head Attention | 词间交互          | 4*d^2     |");
        System.out.println("  | FFN                  | 每个词独立学习     | 8*d^2     |");
        System.out.println("  | LayerNorm            | 稳定训练          | 2*d       |");
        System.out.println("  | Softmax + LM Head    | 概率预测          | d*vocab   |");
        System.out.println("  +-----------------------------------------------------+");
        System.out.println();
        System.out.println("  一句话总结 LLM 原理：");
        System.out.println("    LLM = 深度堆叠的Transformer，通过自注意力机制理解上下文关系，");
        System.out.println("    用自回归方式逐个预测下一个token，从而生成连贯的文本。");
        System.out.println();
        System.out.println("  常见面试题：");
        System.out.println("    Q: Attention为什么除以 sqrt(d_k)？");
        System.out.println("    A: 防止点积结果随维度增长而过大，导致softmax进入梯度饱和区。");
        System.out.println();
        System.out.println("    Q: 为什么用LayerNorm不用BatchNorm？");
        System.out.println("    A: NLP中序列长度可变，BatchNorm在batch维度归一化");
        System.out.println("      对序列长度敏感。LayerNorm在特征维度归一化，不受序列长度影响。");
        System.out.println();
        System.out.println("    Q: Self-Attention的复杂度？");
        System.out.println("    A: O(n^2)，n是序列长度。这是大模型处理长文本的瓶颈。");
        System.out.println();
        System.out.println("    Q: LLM参数量怎么估算？");
        System.out.println("    A: 每层 ~= 4*d^2 (Attention) + 8*d^2 (FFN)");
        System.out.println("      总参数量 = 每层参数量 * 层数 + Embedding参数量");
        System.out.println();
        System.out.println("Demo 结束");
        System.out.println("  完整源码和注释见: src/main/java/me/maiz/llmexplain/");
    }

    private static void waitForUser() {
        System.out.println();
        System.out.println("  按 Enter 继续下一部分...");
        System.out.println("  -----------------------------------------------");
        System.out.println();
        try {
            System.in.read();
            while (System.in.available() > 0) {
                System.in.read(new byte[1024]);
            }
        } catch (Exception e) {
            // ignore
        }
    }
}
