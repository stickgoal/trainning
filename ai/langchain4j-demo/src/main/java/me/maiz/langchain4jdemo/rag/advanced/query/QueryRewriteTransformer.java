package me.maiz.langchain4jdemo.rag.advanced.query;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.rag.query.transformer.QueryTransformer;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 查询重写转换器
 *
 * 在检索前使用 LLM 对用户查询进行重写/扩展，提高检索召回率。
 *
 * 场景举例：
 * - 用户问："多少钱" → 重写为："蜗牛学苑各课程学费价格及优惠信息"
 * - 用户问："怎么报名" → 重写为："蜗牛学苑报名流程、条件和方式"
 */
@Slf4j
public class QueryRewriteTransformer implements QueryTransformer {

    private final ChatModel chatModel;

    public QueryRewriteTransformer(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @Override
    public List<Query> transform(Query query) {
        String originalText = query.text();

        log.info("原始查询: {}", originalText);

        // 使用 LLM 重写查询
        String rewritten = chatModel.chat(
                """
                你是蜗牛学苑知识库的搜索优化专家。
                请将用户的问题改写为更适合向量检索的形式，要求：
                1. 保留核心意图
                2. 补充相关关键词
                3. 使用正式、完整的表达
                4. 控制在 50 字以内
                5. 直接输出改写结果，不要解释

                用户问题：%s
                """.formatted(originalText));

        log.info("重写后查询: {}", rewritten);

        return List.of(Query.from(rewritten.trim()));
    }
}
