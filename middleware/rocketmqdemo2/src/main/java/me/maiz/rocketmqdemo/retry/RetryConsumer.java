package me.maiz.rocketmqdemo.retry;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 消费重试演示：消费失败时返回 {@link ConsumeConcurrentlyStatus#RECONSUME_LATER}，
 * Broker 会按延迟等级重新投递，{@link MessageExt#getReconsumeTimes()} 递增。
 * <p>
 * 演示步骤：
 * 1. 先启动本类
 * 2. 运行 {@link RetrySender}
 * 3. 观察 body 为 {@code fail-until-3} 的消息前 3 次返回 RECONSUME_LATER，第 4 次消费成功
 * <p>
 * 超过 {@link #MAX_RECONSUME_TIMES} 次仍失败时，消息进入死信队列（%DLQ% + consumerGroup）。
 */
public class RetryConsumer {

    public static final String CONSUMER_GROUP = "retry_consumer_group";
    /** 模拟业务：fail-until-3 需要累计重试 3 次后才算成功（即第 4 次投递时 reconsumeTimes=3） */
    private static final int SUCCESS_AFTER_RECONSUME = 3;
    private static final int MAX_RECONSUME_TIMES = 5;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static void main(String[] args) throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(CONSUMER_GROUP);
        consumer.setNamesrvAddr("localhost:9876");
        consumer.setMaxReconsumeTimes(MAX_RECONSUME_TIMES);
        consumer.subscribe(RetrySender.TOPIC, "*");
        consumer.registerMessageListener((MessageListenerConcurrently) (messages, context) -> {
            for (MessageExt message : messages) {
                String body = new String(message.getBody(), StandardCharsets.UTF_8);
                int reconsumeTimes = message.getReconsumeTimes();

                System.out.printf("[%s] msgId=%s key=%s body=%s reconsumeTimes=%d%n",
                        LocalDateTime.now().format(FORMATTER),
                        message.getMsgId(),
                        message.getKeys(),
                        body,
                        reconsumeTimes);

                if (shouldRetry(body, reconsumeTimes)) {
                    System.out.printf("  -> RECONSUME_LATER (will retry, max=%d)%n", MAX_RECONSUME_TIMES);
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
                System.out.println("  -> CONSUME_SUCCESS");
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });

        consumer.start();
        System.out.printf("RetryConsumer started, group=%s, maxReconsumeTimes=%d%n",
                CONSUMER_GROUP, MAX_RECONSUME_TIMES);
        System.out.println("DLQ topic (if exceeded): %DLQ%" + CONSUMER_GROUP);
    }

    private static boolean shouldRetry(String body, int reconsumeTimes) {
        return "fail-until-3".equals(body) && reconsumeTimes < SUCCESS_AFTER_RECONSUME;
    }
}
