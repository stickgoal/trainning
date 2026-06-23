package me.maiz.rocketmqdemo.consuming.clustering;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.protocol.heartbeat.MessageModel;
import java.nio.charset.StandardCharsets;

/**
 * 集群消费（默认模式）：同一 Consumer Group 下多实例分摊消息，每条消息只被一个实例消费。
 * <p>
 * 演示步骤：
 * 1. 先启动本类两次，main 参数分别传入 instance-1、instance-2
 * 2. 再运行 {@link ClusteringSender}
 * 3. 观察两条消息被两个实例分摊，不会重复消费
 */
public class ClusteringConsumer {

    public static final String CONSUMER_GROUP = "clustering_demo_group";

    public static void main(String[] args) throws MQClientException {
        startConsume("instance-1");
        startConsume("instance-2");
    }

    private static void startConsume(String instanceId) throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(CONSUMER_GROUP);
        consumer.setNamesrvAddr("localhost:9876");
        consumer.setMessageModel(MessageModel.CLUSTERING);
        consumer.subscribe(ClusteringSender.TOPIC, "*");
        consumer.registerMessageListener((MessageListenerConcurrently) (messages, context) -> {
            for (MessageExt message : messages) {
                String body = new String(message.getBody(), StandardCharsets.UTF_8);
                System.out.printf("[%s][%s] msgId=%s queueId=%d body=%s%n",
                        instanceId,
                        Thread.currentThread().getName(),
                        message.getMsgId(),
                        message.getQueueId(),
                        body);
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });

        consumer.start();
        System.out.printf("ClusteringConsumer started, group=%s, instance=%s, model=CLUSTERING%n",
                CONSUMER_GROUP, instanceId);
    }
}
