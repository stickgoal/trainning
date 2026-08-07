package com.yourproject.retrieval;

import com.yourproject.config.KnowledgeBaseConfig;
import com.yourproject.config.RAGProperties;
import dev.langchain4j.model.embedding.EmbeddingModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.commands.ProtocolCommand;
import redis.clients.jedis.util.SafeEncoder;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;

/**
 * Redis Stack 混合检索器
 * 核心能力：BM25 文本检索 + 向量相似度检索 + 标签过滤
 * 利用 RediSearch 原生的 FT.SEARCH 命令实现
 *
 * 使用 Jedis 原生命令执行 RediSearch 操作，
 * 避免 Jedis 版本间的 API 差异。
 */
@Component
public class RedisHybridRetriever {

    private static final Logger log = LoggerFactory.getLogger(RedisHybridRetriever.class);

    private final JedisPooled jedis;
    private final EmbeddingModel embeddingModel;
    private final RAGProperties ragProperties;
    private final KnowledgeBaseConfig kbConfig;

    public RedisHybridRetriever(JedisPooled jedis,
                                  EmbeddingModel embeddingModel,
                                  RAGProperties ragProperties,
                                  KnowledgeBaseConfig kbConfig) {
        this.jedis = jedis;
        this.embeddingModel = embeddingModel;
        this.ragProperties = ragProperties;
        this.kbConfig = kbConfig;
    }

    /**
     * 向 Redis 索引写入文档（含 embedding）
     * 使用 RedisJSON 格式存储
     *
     * @param indexName  索引名（仅用于日志，实际由 RediSearch 自动索引 prefix=doc: 的 key）
     * @param docId      文档 ID
     * @param text       文档文本
     * @param embedding  向量
     * @param metadata   元数据
     */
    public void indexDocument(String indexName, String docId, String text,
                              float[] embedding, Map<String, String> metadata) {
        try {
            String redisKey = "doc:" + docId;

            // 构建 RedisJSON 格式的文档
            StringBuilder json = new StringBuilder("{");
            json.append("\"text\":").append(escapeJsonString(text)).append(",");
            json.append("\"embedding\":\"").append(floatArrayToBase64(embedding)).append("\",");

            // metadata 对象
            json.append("\"metadata\":{");
            boolean first = true;
            for (Map.Entry<String, String> entry : metadata.entrySet()) {
                if (!first) json.append(",");
                json.append(escapeJsonString(entry.getKey())).append(":");
                json.append(escapeJsonString(entry.getValue()));
                first = false;
            }
            json.append("}}");

            // 使用 JSON.SET 命令（RedisJSON 模块）
            jedis.sendCommand(cmd("JSON.SET"), redisKey, "$", json.toString());
            log.debug("写入文档: key={}, text={}字符", redisKey, text.length());
        } catch (Exception e) {
            log.error("写入文档失败: docId={}", docId, e);
            throw new RuntimeException("写入文档失败", e);
        }
    }

    /**
     * 向量相似度检索（KNN）
     *
     * @param indexName    索引名
     * @param queryVector  查询向量
     * @param topK         返回数量
     * @return 检索结果列表
     */
    public List<RrfFusion.RetrievalResult> vectorSearch(String indexName, float[] queryVector, int topK) {
        try {
            // RediSearch KNN 查询语法: *=>[KNN K @embedding $vec]
            String query = String.format("*=>[KNN %d @embedding $vec AS vector_score]", topK);

            // 使用原生 FT.SEARCH 命令
            Object response = jedis.sendCommand(cmd("FT.SEARCH"),
                    indexName,
                    query,
                    "PARAMS", "2", "vec", floatArrayToBase64(queryVector),
                    "WITHSCORES",
                    "DIALECT", "2",
                    "LIMIT", "0", String.valueOf(topK),
                    "RETURN", "3", "text", "metadata", "vector_score");

            return parseSearchResponse(response);
        } catch (Exception e) {
            log.error("向量检索失败: index={}", indexName, e);
            return Collections.emptyList();
        }
    }

    /**
     * BM25 文本检索
     *
     * @param indexName  索引名
     * @param queryText  查询文本
     * @param topK       返回数量
     * @return 检索结果列表
     */
    public List<RrfFusion.RetrievalResult> bm25Search(String indexName, String queryText, int topK) {
        try {
            // 转义查询文本中的特殊字符
            String escapedQuery = escapeQuery(queryText);
            String query = String.format("@text:(%s)", escapedQuery);

            Object response = jedis.sendCommand(cmd("FT.SEARCH"),
                    indexName,
                    query,
                    "WITHSCORES",
                    "LIMIT", "0", String.valueOf(topK),
                    "RETURN", "2", "text", "metadata",
                    "DIALECT", "2");

            return parseSearchResponse(response);
        } catch (Exception e) {
            log.error("BM25 检索失败: index={}, query={}", indexName, queryText, e);
            return Collections.emptyList();
        }
    }

    /**
     * 混合检索：向量 + BM25 + RRF 融合
     *
     * @param indexName  索引名
     * @param queryText  查询文本
     * @param topK       最终返回数量
     * @return 融合后的检索结果
     */
    public List<RrfFusion.FusedResult> hybridSearch(String indexName, String queryText, int topK) {
        log.info("混合检索: index={}, query={}, topK={}", indexName, queryText, topK);

        // 1. 生成查询向量
        float[] queryVector = embeddingModel.embed(queryText).content().vector();
        log.debug("查询向量维度: {}", queryVector.length);

        // 2. 并行执行向量检索和 BM25 检索
        int fetchSize = topK * 2;

        List<RrfFusion.RetrievalResult> vectorResults;
        List<RrfFusion.RetrievalResult> bm25Results;

        try {
            vectorResults = vectorSearch(indexName, queryVector, fetchSize);
        } catch (Exception e) {
            log.warn("向量检索异常，降级为仅 BM25", e);
            vectorResults = Collections.emptyList();
        }

        try {
            bm25Results = bm25Search(indexName, queryText, fetchSize);
        } catch (Exception e) {
            log.warn("BM25 检索异常，降级为仅向量检索", e);
            bm25Results = Collections.emptyList();
        }

        log.info("向量检索结果: {} 条, BM25检索结果: {} 条", vectorResults.size(), bm25Results.size());

        // 3. RRF 融合
        List<List<RrfFusion.RetrievalResult>> rankedLists = new ArrayList<>();
        if (!vectorResults.isEmpty()) rankedLists.add(vectorResults);
        if (!bm25Results.isEmpty()) rankedLists.add(bm25Results);

        if (rankedLists.isEmpty()) {
            log.warn("混合检索无结果: index={}, query={}", indexName, queryText);
            return Collections.emptyList();
        }

        RrfFusion fusion = new RrfFusion(ragProperties.getRrf().getK());
        List<RrfFusion.FusedResult> fused = fusion.fuse(rankedLists);

        // 4. 截取 topK
        if (fused.size() > topK) {
            fused = fused.subList(0, topK);
        }

        log.info("混合检索完成: 融合后 {} 条结果", fused.size());
        return fused;
    }

    /**
     * 创建 RediSearch 索引
     *
     * @param indexName  索引名
     * @param dimension  向量维度
     */
    public void createIndex(String indexName, int dimension) {
        // 先尝试删除已有索引
        try {
            jedis.sendCommand(cmd("FT.DROPINDEX"), indexName);
            log.info("已删除旧索引: {}", indexName);
        } catch (Exception ignored) {
            // 索引不存在，忽略
        }

        try {
            // 构建 FT.CREATE 命令参数
            List<String> args = new ArrayList<>();
            args.add(indexName);
            args.add("ON");
            args.add("JSON");
            args.add("PREFIX");
            args.add("1");
            args.add("doc:");
            args.add("SCHEMA");
            args.add("$.text");
            args.add("AS");
            args.add("text");
            args.add("TEXT");
            args.add("WEIGHT");
            args.add("1.0");
            args.add("$.metadata.filename");
            args.add("AS");
            args.add("filename");
            args.add("TEXT");
            args.add("$.metadata.section_title");
            args.add("AS");
            args.add("section");
            args.add("TEXT");
            args.add("$.embedding");
            args.add("AS");
            args.add("embedding");
            args.add("VECTOR");
            args.add("HNSW");
            args.add("6"); // 参数个数：3 对 name-value (TYPE/DIM/DISTANCE_METRIC)
            args.add("TYPE");
            args.add("FLOAT32");
            args.add("DIM");
            args.add(String.valueOf(dimension));
            args.add("DISTANCE_METRIC");
            args.add("COSINE");
            // 注意：RediSearch v2.10.20 不支持 EFCONSTRUCTION/M 参数

            log.info("创建索引: {}, 维度: {}, HNSW (minimal params)", indexName, dimension);

            jedis.sendCommand(cmd("FT.CREATE"), args.toArray(new String[0]));

            log.info("索引创建成功: {}", indexName);
        } catch (Exception e) {
            log.error("创建索引失败: {}", indexName, e);
            throw new RuntimeException("创建索引失败: " + indexName, e);
        }
    }

    /**
     * 清空索引下所有文档
     */
    public void clearIndex(String indexName) {
        try {
            // 删除索引后重建
            createIndex(indexName, kbConfig.getBases().values().iterator().next().getDimension());
            log.info("索引已清空重建: {}", indexName);
        } catch (Exception e) {
            log.error("清空索引失败: {}", indexName, e);
        }
    }

    /**
     * 创建 ProtocolCommand 对象（Jedis 5.x 需要 byte[] 返回值）
     */
    private ProtocolCommand cmd(String command) {
        return () -> SafeEncoder.encode(command);
    }

    // ==================== 解析方法 ====================

    /**
     * 解析 FT.SEARCH 响应为检索结果列表
     * 响应格式（DIALECT 2 + WITHSCORES）:
     * [总数, docId1, score1, [field1, value1, field2, value2, ...], docId2, score2, [...], ...]
     */
    @SuppressWarnings("unchecked")
    private List<RrfFusion.RetrievalResult> parseSearchResponse(Object response) {
        List<RrfFusion.RetrievalResult> results = new ArrayList<>();
        if (response == null) return results;

        try {
            List<Object> list = (List<Object>) response;
            if (list.isEmpty()) return results;

            long total = (long) list.get(0);
            if (total == 0) return results;

            // 遍历结果：每 3 个元素一组 (docId, score, [fields])
            // 但实际格式可能是 (docId, [fields]) 或 (docId, score, [fields]) 取决于 WITHSCORES
            int idx = 1;
            while (idx + 1 < list.size()) {
                String docId = new String((byte[]) list.get(idx));
                double score = 0.0;

                // 检查下一个是 score 还是 fields
                Object next = list.get(idx + 1);
                List<Object> fields;

                if (next instanceof List) {
                    // 无 score，直接是 fields
                    fields = (List<Object>) next;
                    idx += 2;
                } else if (next instanceof String || next instanceof byte[]) {
                    // 有 score
                    String scoreStr = next instanceof byte[] ? new String((byte[]) next) : next.toString();
                    try {
                        score = Double.parseDouble(scoreStr);
                    } catch (NumberFormatException ignored) {}

                    if (idx + 2 < list.size()) {
                        fields = (List<Object>) list.get(idx + 2);
                    } else {
                        fields = Collections.emptyList();
                    }
                    idx += 3;
                } else {
                    idx++;
                    continue;
                }

                // 提取 text 和 metadata
                String text = "";
                Map<String, String> metadata = new HashMap<>();

                if (fields != null) {
                    for (int i = 0; i + 1 < fields.size(); i += 2) {
                        String key = fields.get(i) instanceof byte[] ? new String((byte[]) fields.get(i)) : fields.get(i).toString();
                        Object valObj = fields.get(i + 1);
                        String val = valObj instanceof byte[] ? new String((byte[]) valObj) : valObj.toString();

                        if ("text".equals(key)) {
                            text = val;
                        } else if (key.startsWith("metadata.")) {
                            metadata.put(key.substring("metadata.".length()), val);
                        } else if ("$".equals(key) || key.startsWith("$.")) {
                            // RedisJSON 可能返回完整 JSON
                            try {
                                // 尝试解析 JSON 获取 text
                                if (val.contains("\"text\"")) {
                                    int textIdx = val.indexOf("\"text\"");
                                    int colonIdx = val.indexOf(":", textIdx);
                                    int startQ = val.indexOf("\"", colonIdx + 1);
                                    int endQ = val.indexOf("\"", startQ + 1);
                                    if (startQ >= 0 && endQ > startQ) {
                                        text = val.substring(startQ + 1, endQ);
                                    }
                                }
                            } catch (Exception ignored) {}
                        } else if (!"vector_score".equals(key) && !"id".equals(key)) {
                            metadata.put(key, val);
                        }
                    }
                }

                results.add(new RrfFusion.RetrievalResult(docId, text, score, metadata));
            }
        } catch (Exception e) {
            log.error("解析检索结果失败", e);
        }

        return results;
    }

    // ==================== 工具方法 ====================

    /**
     * 将 float 数组转换为 Base64 字符串（RediSearch 向量格式）
     * 使用 Little Endian 字节序
     */
    private String floatArrayToBase64(float[] floats) {
        byte[] bytes = floatArrayToBytes(floats);
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * 将 float 数组转换为 byte 数组（Little Endian）
     */
    private byte[] floatArrayToBytes(float[] floats) {
        ByteBuffer buffer = ByteBuffer.allocate(floats.length * 4);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        for (float f : floats) {
            buffer.putFloat(f);
        }
        return buffer.array();
    }

    /**
     * 转义 RediSearch 查询文本中的特殊字符
     */
    private String escapeQuery(String query) {
        return query.replace("\"", "\\\"")
                    .replace("'", "\\'")
                    .replace(":", "\\:")
                    .replace("/", "\\/")
                    .replace("(", "\\(")
                    .replace(")", "\\)")
                    .replace("[", "\\[")
                    .replace("]", "\\]")
                    .replace(" ", " | ");
    }

    /**
     * 转义 JSON 字符串
     */
    private String escapeJsonString(String str) {
        if (str == null) return "";
        return "\"" + str.replace("\\", "\\\\")
                         .replace("\"", "\\\"")
                         .replace("\n", "\\n")
                         .replace("\r", "\\r")
                         .replace("\t", "\\t") + "\"";
    }
}
