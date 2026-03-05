package me.maiz.logcollection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/example")
public class ExampleController {
    
    private static final Logger logger = LoggerFactory.getLogger(ExampleController.class);
    
    @GetMapping("/operation1")
    public String performOperation1() {
        logger.info("开始执行操作1");
        try {
            // 执行具体业务逻辑
            String result = "操作1执行成功";
            logger.info("操作1完成，结果: {}", result);
            return result;
        } catch (Exception e) {
            logger.error("操作1执行失败", e);
            return "操作失败";
        }
    }
    
    @PostMapping("/operation2")
    public String performOperation2(@RequestBody String data) {
        logger.info("开始执行操作2，接收数据: {}", data);
        try {
            // 模拟处理数据
            String processedData = data.toUpperCase();
            logger.debug("数据处理完成: {} -> {}", data, processedData);
            
            // 执行其他操作
            logger.info("操作2执行完毕");
            return "处理后的数据: " + processedData;
        } catch (Exception e) {
            logger.error("操作2执行过程中发生错误，输入数据: {}", data, e);
            return "数据处理失败";
        }
    }
    
    @PutMapping("/operation3/{id}")
    public String performOperation3(@PathVariable Long id, @RequestParam String status) {
        logger.info("开始执行操作3，ID: {}, 状态: {}", id, status);
        
        // 模拟更新操作
        logger.info("更新ID为{}的记录状态为{}", id, status);
        
        return "记录 " + id + " 状态已更新为 " + status;
    }
}
