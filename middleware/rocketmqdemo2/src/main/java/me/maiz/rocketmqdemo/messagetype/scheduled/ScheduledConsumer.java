package me.maiz.rocketmqdemo.messagetype.scheduled;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 定时消息消费者：消息会在生产者设置的 {@code deliverTimeMs} 之后才投递到消费者。
 * <p>
 * 演示步骤：
 * 1. 先启动本类
 * 2. 再运行 {@link ScheduledSender}
 * 3. 观察消费时间接近预设的投递时间，而非发送时间
 */
public class ScheduledConsumer {

    public static final String CONSUMER_GROUP = "scheduled_consumer_group";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(CONSUMER_GROUP);
        consumer.setNamesrvAddr("localhost:9876");
        consumer.subscribe(ScheduledSender.TOPIC, "*");
        consumer.registerMessageListener((MessageListenerConcurrently) (messages, context) -> {
            long now = System.currentTimeMillis();
            for (MessageExt message : messages) {
                String body = new String(message.getBody(), StandardCharsets.UTF_8);
                long deliverTimeMs = message.getDeliverTimeMs();
                long bornTimestamp = message.getBornTimestamp();
                long delayFromBorn = now - bornTimestamp;
                long delayFromPlan = now - deliverTimeMs;

                System.out.printf("consumeAt=%s, planDeliverAt=%s, msgId=%s%n",
                        formatTime(now),
                        deliverTimeMs > 0 ? formatTime(deliverTimeMs) : "N/A",
                        message.getMsgId());
                System.out.printf("  body=%s, bornToConsume=%dms, planToConsume=%dms%n",
                        body, delayFromBorn, delayFromPlan);
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });

        consumer.start();
        System.out.printf("ScheduledConsumer started, group=%s, topic=%s%n",
                CONSUMER_GROUP, ScheduledSender.TOPIC);
    }

    private static String formatTime(long epochMs) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMs), ZoneId.systemDefault()).format(FORMATTER);
    }
}
