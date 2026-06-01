package me.maiz.middleware.rocketmqbootdemo.sending;
import java.util.Date;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
public class LoginController {

    @Resource
    private RocketMQTemplate rocketMQTemplate;
    
    @Resource
    private MessageSendService messageSendService;

    @RequestMapping("/login")
    public String login(String username,String password){
        log.info("登录，用户名={}",username);
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(12);
        userInfo.setUsername("weqeq");
        userInfo.setMobile("1333333333333");
        userInfo.setAvatar("asdfasdfasdf");
        userInfo.setRegTime(new Date());
        
        // 演示各种消息发送方式
        messageSendService.sendSyncMessage(userInfo);           // 同步发送
        messageSendService.sendAsyncMessage(userInfo);          // 异步发送
        messageSendService.sendOnewayMessage(userInfo);         // Oneway 发送
        messageSendService.sendOrderlyMessage(userInfo, "order_123");  // 顺序消息
        messageSendService.sendDelayMessage(userInfo, 3);       // 延迟消息（10秒）
        messageSendService.sendTransactionMessage(userInfo, null);  // 事务消息
        
        return "success";
    }

}
