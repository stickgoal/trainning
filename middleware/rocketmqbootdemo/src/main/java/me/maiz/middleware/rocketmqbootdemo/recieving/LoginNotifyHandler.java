package me.maiz.middleware.rocketmqbootdemo.recieving;

import lombok.extern.slf4j.Slf4j;
import me.maiz.middleware.rocketmqbootdemo.sending.UserInfo;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.common.admin.ConsumeStats;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQReplyListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
//请注意是在类上加注解，实现接口
@RocketMQMessageListener(topic = "login-success-topic",consumerGroup = "${rocketmq.consumer.group}")
public class LoginNotifyHandler implements RocketMQListener<UserInfo> {
    @Override
    public void onMessage(UserInfo message) {
        log.info("收到消息：{}",message);

    }
}
