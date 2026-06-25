package me.maiz.llmexplain;

import java.util.*;

/**
 * =============================================
 * 1. Tokenizer（分词器）— LLM 理解语言的"第一步"
 * =============================================
 * <p>
 * 大语言模型不认识文字，它只认识「数字」。
 * Tokenizer 的作用就是把人类语言拆分成 token（词元），
 * 然后给每个 token 分配一个唯一编号（token ID）。
 * <p>
 * 面试考点：
 * 1. 为什么需要分词？ — 模型只能处理数字，不能处理文字
 * 2. 常见分词方法：Word-Level（词级别）、BPE（字节对编码）、
 *    WordPiece、SentencePiece
 * 3. BPE 的核心思想：从字符开始，逐步合并最频繁出现的字符对
 * 4. 词表大小一般在 32K-100K 之间（GPT-4 约 100K）
 * 5. 中文分词的挑战：没有空格分隔，需要子词粒度
 * <p>
 * 本Demo用一个简化的词表来演示基本原理。
 */
public class SimpleTokenizer {

    /**
     * 词表 (Vocabulary) — 一个简单的"中文 + 英文"混合词表
     * <p>
     * 词表中的每个词都对应一个唯一的数字ID。
     * 这就像新华字典给每个字编了页码。
     */
    private final Map<String, Integer> wordToId = new LinkedHashMap<>();
    private final Map<Integer, String> idToWord = new HashMap<>();

    /**
     * 特殊token — 所有LLM都有这些特殊标记
     */
    public static final int PAD_ID = 0;    // <PAD>: 填充，用于将不同长度的序列对齐
    public static final int EOS_ID = 1;    // <EOS>: 结束符，表示文本结束
    public static final int UNK_ID = 2;    // <UNK>: 未知词，词表中不存在的词

    public SimpleTokenizer() {
        // 添加特殊 token
        wordToId.put("<PAD>", PAD_ID);
        wordToId.put("<EOS>", EOS_ID);
        wordToId.put("<UNK>", UNK_ID);

        // 添加常用中文词
        String[] words = {
                "我", "你", "他", "她", "们",        // 代词
                "爱", "喜欢", "学习", "研究", "工作",    // 动词
                "人工智能", "机器", "语言", "模型", "数据", // 名词
                "很", "非常", "也", "和", "的",         // 修饰/连接
                "是", "有", "要", "会", "能",           // 助动词
                "好", "大", "小", "新", "高",           // 形容词
                "中国", "美国", "科技", "未来", "世界",   // 专有名词
                "不", "吗", "啊", "了", "在"            // 其他常用
        };
        for (String w : words) {
            if (!wordToId.containsKey(w)) {
                int id = wordToId.size();
                wordToId.put(w, id);
            }
        }

        // 构建反向映射 (ID → 词)
        for (Map.Entry<String, Integer> entry : wordToId.entrySet()) {
            idToWord.put(entry.getValue(), entry.getKey());
        }
    }

    /**
     * 获取词表大小
     */
    public int vocabSize() {
        return wordToId.size();
    }

    /**
     * 将单个词转为 ID
     * 如果词不在词表中，返回 UNK_ID
     */
    public int encodeToken(String word) {
        return wordToId.getOrDefault(word, UNK_ID);
    }

    /**
     * 将 ID 转回词
     */
    public String decode(int id) {
        return idToWord.getOrDefault(id, "<UNK>");
    }

    /**
     * 将一句话编码为 token ID 序列（最长匹配优先）
     * <p>
     * 从句子开头开始，尽可能匹配最长的词表词。
     * 匹配不上时，逐字回退（模拟BPE的回退策略）。
     * <p>
     * 注意：真正的LLM使用BPE等子词分词器，一个词可能被拆成多个token。
     * 例如 "人工智能" 在 GPT 中可能被拆成 "人工" + "智能" 两个token。
     * 这里演示了"最长前缀匹配"的基本思想。
     */
    public int[] encode(String sentence) {
        List<Integer> ids = new ArrayList<>();
        int i = 0;
        while (i < sentence.length()) {
            boolean matched = false;
            // 尝试从当前位置匹配最长的词
            for (int end = sentence.length(); end > i; end--) {
                String sub = sentence.substring(i, end);
                if (wordToId.containsKey(sub)) {
                    ids.add(wordToId.get(sub));
                    i = end;
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                // 没有匹配到任何词表中的词，用单个字符 + UNK
                String ch = sentence.substring(i, i + 1);
                ids.add(UNK_ID);
                i++;
            }
        }
        ids.add(EOS_ID);
        return ids.stream().mapToInt(id -> id).toArray();
    }

    /**
     * 将 token ID 序列解码回文本
     */
    public String decode(int[] ids) {
        StringBuilder sb = new StringBuilder();
        for (int id : ids) {
            if (id == PAD_ID || id == EOS_ID) continue;
            sb.append(decode(id));
        }
        return sb.toString();
    }

    /**
     * 打印词表
     */
    public void printVocabulary() {
        System.out.println("========== 词表 (Vocabulary) ==========");
        System.out.println("词表中的每个词都有一个唯一的数字ID：");
        System.out.println("----------------------------------------------");
        System.out.printf("%-6s | %-12s | %-20s%n", "ID", "Token", "说明");
        System.out.println("----------------------------------------------");
        for (Map.Entry<String, Integer> entry : wordToId.entrySet()) {
            String note = "";
            if (entry.getKey().equals("<PAD>")) note = "填充符，补齐序列长度";
            else if (entry.getKey().equals("<EOS>")) note = "结束符，文本到此结束";
            else if (entry.getKey().equals("<UNK>")) note = "未知词，词表中没有";
            System.out.printf("%-6d | %-12s | %-20s%n", entry.getValue(), entry.getKey(), note);
        }
        System.out.println("----------------------------------------------");
        System.out.println("词表总大小: " + vocabSize() + " tokens\n");
    }

    /**
     * 演示 tokenizer 的工作流程
     */
    public void demo() {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║       第1步：Tokenizer（分词器）— 文字→数字           ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
        System.out.println();

        // 先打印词表
        printVocabulary();

        // 演示编码过程
        String sentence = "我爱学习";
        System.out.println("▶ 编码过程演示");
        System.out.println("  输入句子: \"" + sentence + "\"");
        System.out.println();
        System.out.println("  LLM看不懂文字，需要把每个字变成数字：");
        System.out.println();

        int[] ids = encode(sentence);
        // 构建 token -> ID 的映射表（按token展示）
        List<String> tokenList = new ArrayList<>();
        int pos = 0;
        while (pos < sentence.length()) {
            boolean found = false;
            for (int end = sentence.length(); end > pos; end--) {
                String sub = sentence.substring(pos, end);
                if (wordToId.containsKey(sub)) {
                    tokenList.add(sub + "(" + wordToId.get(sub) + ")");
                    pos = end;
                    found = true;
                    break;
                }
            }
            if (!found) {
                tokenList.add("'" + sentence.substring(pos, pos+1) + "'=<UNK>");
                pos++;
            }
        }
        for (String t : tokenList) {
            System.out.println("    " + t);
        }
        System.out.println("    <EOS>  →  ID=" + EOS_ID + " (句子结束标记)");
        System.out.println();

        // 整体打印
        System.out.print("  最终编码结果: ");
        for (int i = 0; i < ids.length; i++) {
            System.out.print(ids[i]);
            if (i < ids.length - 1) System.out.print(", ");
        }
        System.out.println();
        System.out.println("  这串数字就是LLM真正看到的\"内容\"。");
        System.out.println();

        // 演示解码过程
        System.out.println("▶ 解码过程演示 (数字→文字)：");
        String decoded = decode(ids);
        System.out.println("  [" + Arrays.toString(ids) + "]  →  \"" + decoded + "\"");
        System.out.println();

        // 面试知识点
        System.out.println("----------------------------------------");
        System.out.println("💡 面试重点：");
        System.out.println("  1. Tokenization是LLM的第一个步骤");
        System.out.println("  2. GPT系列使用BPE(Byte-Pair Encoding)分词");
        System.out.println("  3. BPE从字符开始，逐步合并高频字符对");
        System.out.println("  4. 一个中文汉字≈1个token，英文单词≈1-3个token");
        System.out.println("  5. 词表中的<PAD>、<EOS>、<UNK>是所有LLM共有的");
        System.out.println("----------------------------------------\n");
    }
}
