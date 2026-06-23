package me.maiz.rocketmqdemo.retry;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

import java.nio.charset.StandardCharsets;

/**
 * 消费重试演示：发送会触发重试与直接成功的消息。
 */
public class RetrySender {

    public static final String TOPIC = "TopicRetry";

    public static void main(String[] args) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("retry_producer_group");
        producer.setNamesrvAddr("localhost:9876");
        producer.start();

        send(producer, "success-1", "直接消费成功");
        send(producer, "retry-3", "fail-until-3");
        send(producer, "success-2", "直接消费成功");

        producer.shutdown();
        System.out.println("RetrySender finished.");
    }

    private static void send(DefaultMQProducer producer, String key, String body) throws Exception {
        Message msg = new Message(TOPIC, "TagRetry", key, body.getBytes(StandardCharsets.UTF_8));
        SendResult result = producer.send(msg);
        System.out.printf("sent key=%s, body=%s, msgId=%s%n", key, body, result.getMsgId());
    }
}
