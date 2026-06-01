package me.maiz.middleware.rocketmqbootdemo.sending;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 消息发送测试控制器
 * 提供独立接口测试各种消息发送方式
 */
@RestController
@RequestMapping("/message")
@Slf4j
public class MessageTestController {

    @Resource
    private MessageSendService messageSendService;

    /**
     * 创建测试用的 UserInfo
     */
    private UserInfo createTestUserInfo() {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(100);
        userInfo.setUsername("testuser");
        userInfo.setMobile("13800138000");
        userInfo.setAvatar("https://example.com/avatar.png");
        userInfo.setRegTime(new Date());
        return userInfo;
    }

    /**
     * 测试同步发送
     */
    @RequestMapping("/sync")
    public String testSyncSend() {
        log.info("测试同步发送消息");
        UserInfo userInfo = createTestUserInfo();
        messageSendService.sendSyncMessage(userInfo);
        return "同步发送完成";
    }

    /**
     * 测试异步发送
     */
    @RequestMapping("/async")
    public String testAsyncSend() {
        log.info("测试异步发送消息");
        UserInfo userInfo = createTestUserInfo();
        messageSendService.sendAsyncMessage(userInfo);
        return "异步发送请求已发出，请查看日志";
    }

    /**
     * 测试 Oneway 发送
     */
    @RequestMapping("/oneway")
    public String testOnewaySend() {
        log.info("测试 Oneway 发送消息");
        UserInfo userInfo = createTestUserInfo();
        messageSendService.sendOnewayMessage(userInfo);
        return "Oneway 发送完成";
    }

    /**
     * 测试顺序消息发送
     * @param orderKey 业务标识，默认为 order_001
     */
    @RequestMapping("/orderly")
    public String testOrderlySend(@RequestParam(required = false, defaultValue = "order_001") String orderKey) {
        log.info("测试顺序消息发送，orderKey={}", orderKey);
        UserInfo userInfo = createTestUserInfo();
        messageSendService.sendOrderlyMessage(userInfo, orderKey);
        return "顺序消息发送完成，orderKey=" + orderKey;
    }

    /**
     * 测试延迟消息发送
     * @param delayLevel 延迟级别（1-18），默认为 3（10秒）
     */
    @RequestMapping("/delay")
    public String testDelaySend(@RequestParam(required = false, defaultValue = "3") int delayLevel) {
        log.info("测试延迟消息发送，delayLevel={}", delayLevel);
        UserInfo userInfo = createTestUserInfo();
        messageSendService.sendDelayMessage(userInfo, delayLevel);
        return "延迟消息发送完成，延迟级别=" + delayLevel;
    }

    /**
     * 测试事务消息发送
     */
    @RequestMapping("/transaction")
    public String testTransactionSend() {
        log.info("测试事务消息发送");
        UserInfo userInfo = createTestUserInfo();
        messageSendService.sendTransactionMessage(userInfo, null);
        return "事务消息发送完成";
    }
}
