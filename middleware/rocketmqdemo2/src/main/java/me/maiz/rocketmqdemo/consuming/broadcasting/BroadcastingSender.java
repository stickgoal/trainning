package me.maiz.rocketmqdemo.consuming.broadcasting;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

/**
 * 广播消费演示：向 TopicBroadcasting 发送多条消息。
 * 启动两个 {@link BroadcastingConsumer}（同一 Consumer Group）后，每个实例都会收到全部消息。
 */
public class BroadcastingSender {

    public static final String TOPIC = "TopicBroadcasting";

    public static void main(String[] args) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("broadcasting_producer_group");
        producer.setNamesrvAddr("localhost:9876");
        producer.start();

        int count = args.length > 0 ? Integer.parseInt(args[0]) : 10;
        for (int i = 0; i < count; i++) {
            String body = "broadcasting message #" + i;
            Message msg = new Message(TOPIC, "TagBroadcasting", body.getBytes(RemotingHelper.DEFAULT_CHARSET));
            SendResult result = producer.send(msg);
            System.out.printf("sent [%s] %s%n", body, result.getMsgId());
        }

        producer.shutdown();
        System.out.println("BroadcastingSender finished, total=" + count);
    }
}
