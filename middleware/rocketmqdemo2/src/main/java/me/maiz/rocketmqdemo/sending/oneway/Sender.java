package me.maiz.rocketmqdemo.sending.oneway;


import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.util.Scanner;

public class Sender {
    public static void main(String[] args) throws Exception {
        // 实例化消息生产者Producer
        DefaultMQProducer producer = new DefaultMQProducer("TestSending");
        // 设置NameServer的地址
        producer.setNamesrvAddr("localhost:9876");
        // 启动Producer实例
        producer.start();
//        for (int i = 0; i < 100; i++) {
        Scanner scanner = new Scanner(System.in);
        while (true){
        System.out.println("请输入消息内容");
        String message = scanner.next();
        // 创建消息，并指定Topic，Tag和消息体
            Message msg = new Message("TopicTest" /* Topic */,
                    "TagA" /* Tag */,
                    "orderId123",
                    (message).getBytes(RemotingHelper.DEFAULT_CHARSET) /* Message body */
            );
            // 发送消息到一个Broker
             producer.sendOneway(msg);
            // 通过sendResult返回消息是否成功送达
            System.out.printf("发送完成");
        }
        // 如果不再发送消息，关闭Producer实例。
//        producer.shutdown();
    }
}
