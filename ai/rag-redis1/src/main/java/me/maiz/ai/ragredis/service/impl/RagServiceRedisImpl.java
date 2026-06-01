package me.maiz.ai.ragredis.service.impl;

import me.maiz.ai.ragredis.param.ChatParam;
import me.maiz.ai.ragredis.service.RagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RagServiceRedisImpl implements RagService {
    public static final int TOP_K=1;
    public static final double SIMILARITY_THRESHOLD = 0.7;
    private static final Logger logger = LoggerFactory.getLogger(RagServiceRedisImpl.class);

    // 声明 ChatClient 实例，用于与 AI 模型进行交互
    private final ChatClient chatClient;

    // 构造函数，通过注入 ChatClient.Builder 来构建 ChatClient 实例
    public RagServiceRedisImpl(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }



    @Autowired
    private VectorStore vectorStore;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void importDocuments() {
        // 默认导入逻辑，可以根据需要实现
        logger.info("执行默认文档导入");
    }
    
    @Override
    public void processFile(Resource fileResource) {
        logger.info("开始处理文件资源: {}", fileResource.getFilename());
        importDocuments(fileResource);
    }
    
    public void importDocuments(Resource fileResource) {
        TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(fileResource);
        List<Document> documentList = tikaDocumentReader.get();
        logger.info("加载到 {} 个文档", documentList.size());
        TokenTextSplitter textSplitter = new TokenTextSplitter();
        List<Document> splitedDocumentList = textSplitter.split(documentList);
        logger.info("拆分后有 {} 个文档", splitedDocumentList.size());
        logDocumentList(splitedDocumentList);
        vectorStore.add(splitedDocumentList);
    }

    private void logDocumentList(List<Document> splitedDocumentList) {
        splitedDocumentList.stream().forEach(d->{
            logger.debug("id:{},元数据：{}", d.getId(),d.getMetadata());
            logger.debug("内容：{}", d.getText());
        });
    }

    public void removeDocuments() {

    }
    @Override
    public String retrieve(String question) {
        logger.info("处理问题：{}",question);
        List<Document> documentList = vectorStore.similaritySearch(SearchRequest.builder()
                .query(question)
                .topK(TOP_K)
                .similarityThreshold(SIMILARITY_THRESHOLD).build());
        logDocumentList(documentList);
        String context = documentList.stream().map(document -> document.getText()).collect(Collectors.joining("\n--->"));
        return  chatClient.prompt()
                .user(question)
                .system("你是个人工客服，根据用户提问回答问题，但必须严格基于给定的参考文档来回答。如果文档里没有，则直接回复:‘您的问题暂时无法回答您，请来电咨询~’，不要自己捏造")
                .advisors(advisor -> advisor.param("question_answer_context", context))
                .call().content();

    }

    /**
     * RAG查询 - 使用Spring AI内置Advisor（推荐）
     */
    @Override
    public String queryWithAdvisor(String question) {
        return chatClient.prompt()
                .user(question)
                .advisors(QuestionAnswerAdvisor.builder(vectorStore)
                        .searchRequest( SearchRequest.builder()
                        .query(question)
                        .topK(TOP_K)
                        .similarityThreshold(SIMILARITY_THRESHOLD)
                        .build()).build()
                       )
                .call()
                .content();
    }

    @Override
    public List<ChatParam> getConversationHistory(String conversationId) {

        // 从数据库查询该会话的所有消息，按时间顺序排列
        String sql = "SELECT content, type FROM SPRING_AI_CHAT_MEMORY WHERE conversation_id = ? ORDER BY timestamp ASC";

        return jdbcTemplate.query(sql, new Object[]{conversationId}, (rs, rowNum) -> {
            ChatParam chatParam = new ChatParam();
            chatParam.setRole(rs.getString("type").toLowerCase());
            chatParam.setContent(rs.getString("content"));
            return chatParam;
        });
    }
}
