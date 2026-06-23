package me.maiz.rocketmqdemo.consuming.clustering;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

/**
 * 集群消费演示：向 TopicClustering 发送多条消息。
 * 启动两个 {@link ClusteringConsumer}（同一 Consumer Group）后，每条消息只会被其中一个实例消费。
 */
public class ClusteringSender {

    public static final String TOPIC = "TopicClustering";

    public static void main(String[] args) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("clustering_producer_group");
        producer.setNamesrvAddr("localhost:9876");
        producer.start();

        int count = args.length > 0 ? Integer.parseInt(args[0]) : 20;
        for (int i = 0; i < count; i++) {
            String body = "clustering message #" + i;
            Message msg = new Message(TOPIC, "TagClustering", body.getBytes(RemotingHelper.DEFAULT_CHARSET));
            SendResult result = producer.send(msg);
            System.out.printf("sent [%s] %s%n", body, result.getMsgId());
        }

        producer.shutdown();
        System.out.println("ClusteringSender finished, total=" + count);
    }
}
