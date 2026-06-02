package me.maiz.ai.ragredis.controller;

import lombok.extern.slf4j.Slf4j;
import me.maiz.ai.ragredis.param.ChatParam;
import me.maiz.ai.ragredis.param.PageResponse;
import me.maiz.ai.ragredis.service.RagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@Slf4j
public class RAGController {

    @Autowired
    private RagService ragService;


    /**
     * 文件上传及导入
     */
    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            log.info("开始处理文件: {}", file.getOriginalFilename());

            // 将 MultipartFile 转换为 Spring Resource
            ragService.processFile(toSpringResource(file));
            return "文件上传并处理成功";
        } catch (IOException e) {
            log.error("文件处理失败", e);
            return "文件处理失败: " + e.getMessage();
        }
    }

    private static InputStreamResource toSpringResource(MultipartFile file) throws IOException {
        InputStreamResource resource = new InputStreamResource(file.getInputStream()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }

            @Override
            public long contentLength() {
                return file.getSize();
            }
        };
        return resource;
    }

    @GetMapping("/chat/rag")
    public String chat(@RequestParam("question") String question) {
        log.info("处理问题：{}", question);
        return ragService.retrieve(question);
//        return ragService.queryWithAdvisor(question);
    }

    /**
     * 根据conversationId查询所有会话信息，按时间顺序排列
     */
    @GetMapping("/chat/history")
    public List<ChatParam> getConversationHistory(@RequestParam("conversationId") String conversationId) {
        log.info("查询会话历史，conversationId: {}", conversationId);
        return ragService.getConversationHistory(conversationId);
    }

    /**
     * 分页查询会话历史记录
     */
    @GetMapping("/chat/history/page")
    public PageResponse<ChatParam> getConversationHistoryPage(
            @RequestParam("conversationId") String conversationId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("分页查询会话历史，conversationId: {}, page: {}, size: {}", conversationId, page, size);
        return ragService.getConversationHistoryPage(conversationId, page, size);
    }



}
