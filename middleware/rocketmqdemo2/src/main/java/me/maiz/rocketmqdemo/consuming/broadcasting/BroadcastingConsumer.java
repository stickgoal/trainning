package me.maiz.rocketmqdemo.consuming.broadcasting;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.protocol.heartbeat.MessageModel;

import java.nio.charset.StandardCharsets;

/**
 * 广播消费：同一 Consumer Group 下每个实例都会收到 Topic 中的全部消息。
 * <p>
 * 演示步骤：
 * 1. 先启动本类两次，main 参数分别传入 instance-1、instance-2
 * 2. 再运行 {@link BroadcastingSender}
 * 3. 观察同一条消息被两个实例各消费一次
 */
public class BroadcastingConsumer {

    public static final String CONSUMER_GROUP = "broadcasting_demo_group";

    public static void main(String[] args) throws MQClientException {
        start("instance-1");
        start("instance-2");
    }

    private static void start(String instanceId) throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(CONSUMER_GROUP+instanceId);
        consumer.setNamesrvAddr("localhost:9876");
        consumer.setMessageModel(MessageModel.BROADCASTING);
        consumer.subscribe(BroadcastingSender.TOPIC, "*");

        consumer.registerMessageListener((MessageListenerConcurrently) (messages, context) -> {
            for (MessageExt message : messages) {
                String body = new String(message.getBody(), StandardCharsets.UTF_8);
                System.out.printf("[%s][%s] msgId=%s body=%s%n",
                        instanceId,
                        Thread.currentThread().getName(),
                        message.getMsgId(),
                        body);
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });

        consumer.start();
        System.out.printf("BroadcastingConsumer started, group=%s, instance=%s, model=BROADCASTING%n",
                CONSUMER_GROUP, instanceId);
    }
}
