package me.maiz.ai.ragredis.service;

import me.maiz.ai.ragredis.param.ChatParam;
import me.maiz.ai.ragredis.param.PageResponse;
import org.springframework.core.io.Resource;

import java.util.List;

public interface RagService {
    /**
     * 导入文档到向量数据库
     */
    void importDocuments();

    /**
     * 处理上传的文件资源并导入到向量数据库
     *
     * @param resource 文件资源
     */
    void processFile(Resource resource);

    /**
     * 检索文档
     *
     * @param question
     * @return 响应结果
     */
    String retrieve(String question);

    String queryWithAdvisor(String question);

    /**
     * 根据conversationId查询所有会话信息，按时间顺序排列
     *
     * @param conversationId 会话ID
     * @return 会话消息列表
     */
    List<ChatParam> getConversationHistory(String conversationId);

    /**
     * 根据conversationId分页查询会话历史，按时间顺序排列
     *
     * @param conversationId 会话ID
     * @param page           页码（从1开始）
     * @param size           每页大小
     * @return 分页的会话消息列表
     */
    PageResponse<ChatParam> getConversationHistoryPage(String conversationId, int page, int size);
}


