package me.maiz.middleware.rocketmqbootdemo.sending;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.util.Date;

/**
 * RocketMQ 消息发送服务
 * 演示各种消息发送方式
 */
@Service
@Slf4j
public class MessageSendService {

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 1. 同步发送消息
     * 适用于重要通知、短信等场景，需要确保消息成功发送
     */
    public void sendSyncMessage(UserInfo userInfo) {
        SendResult sendResult = rocketMQTemplate.syncSend("login-success-topic", userInfo);
        log.info("同步发送成功：{}", sendResult);
    }

    /**
     * 2. 异步发送消息
     * 适用于对响应时间敏感的业务，发送后立即返回，通过回调处理结果
     */
    public void sendAsyncMessage(UserInfo userInfo) {
        rocketMQTemplate.asyncSend("login-success-topic", userInfo, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("异步发送成功：{}", sendResult);
            }

            @Override
            public void onException(Throwable e) {
                log.warn("异步发送失败", e);
            }
        });
        log.info("异步发送请求已发出");
    }

    /**
     * 3. Oneway 发送消息
     * 只发送不等待响应，适用于日志收集等不重要的场景
     */
    public void sendOnewayMessage(UserInfo userInfo) {
        rocketMQTemplate.sendOneWay("login-success-topic", userInfo);
        log.info("Oneway 发送完成");
    }

    /**
     * 4. 顺序消息发送
     * 保证同一业务标识的消息按顺序消费，如订单状态变更
     * 
     * @param userInfo 用户信息
     * @param orderKey 业务标识（如订单ID），相同key的消息会发送到同一队列
     */
    public void sendOrderlyMessage(UserInfo userInfo, String orderKey) {
        SendResult sendResult = rocketMQTemplate.syncSendOrderly(
            "order-topic", 
            userInfo, 
            orderKey
        );
        log.info("顺序消息发送成功：{}", sendResult);
    }

    /**
     * 5. 定时/延迟消息发送
     * 消息会在指定延迟时间后投递给消费者
     * 
     * 延迟级别对照表：
     * 1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
     * 1  2  3   4   5  6  7  8  9  10 11 12 13 14  15  16  17 18
     * 
     * @param userInfo 用户信息
     * @param delayLevel 延迟级别（1-18）
     */
    public void sendDelayMessage(UserInfo userInfo, int delayLevel) {
        Message<UserInfo> message = MessageBuilder.withPayload(userInfo).build();
        SendResult sendResult = rocketMQTemplate.syncSend(
            "delay-topic", 
            message, 
            3000,      // 超时时间（毫秒）
            delayLevel // 延迟级别
        );
        log.info("延迟消息发送成功，延迟级别：{}，结果：{}", delayLevel, sendResult);
    }

    public void sendFixTimeMessage(UserInfo userInfo, Date time) {
        Message<UserInfo> message = MessageBuilder.withPayload(userInfo).build();
        SendResult sendResult = rocketMQTemplate.syncSendDeliverTimeMills("delay-topic",userInfo, time.getTime());
        log.info("定时消息发送成功，延迟级别：{}，结果：{}", DateFormat.getDateInstance(2).format(time), sendResult);
    }

    /**
     * 6. 事务消息发送
     * 适用于需要保证本地事务和消息发送一致性的场景
     * 需要配合 TransactionListener 实现类使用
     * 
     * @param userInfo 用户信息
     * @param businessArg 业务参数，传递给 TransactionListener
     */
    public void sendTransactionMessage(UserInfo userInfo, Object businessArg) {
        // 注意：TransactionSendResult 在不同版本中包路径可能不同
        // 如果编译错误，请注释掉此方法或升级 RocketMQ 版本
        try {
            Object transactionSendResult = rocketMQTemplate.sendMessageInTransaction(
                "transaction-topic",
                MessageBuilder.withPayload(userInfo).build(),
                businessArg
            );
            log.info("事务消息发送完成：{}", transactionSendResult);
        } catch (Exception e) {
            log.error("事务消息发送失败", e);
        }
    }

    /**
     * 创建测试用的 UserInfo 对象
     */
    private UserInfo createTestUserInfo() {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(12);
        userInfo.setUsername("testuser");
        userInfo.setMobile("13333333333");
        userInfo.setAvatar("avatar_url");
        userInfo.setRegTime(new Date());
        return userInfo;
    }
}
