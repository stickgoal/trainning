package me.maiz.rocketmqdemo.sending.async;


import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.util.Random;
import java.util.Scanner;

public class Sender {
    public static void main(String[] args) throws Exception {
        // 实例化消息生产者Producer
        DefaultMQProducer producer = new DefaultMQProducer("TestSending");
        // 设置NameServer的地址
        producer.setNamesrvAddr("localhost:9876");

        // 启动Producer实例
        producer.start();
        producer.setRetryTimesWhenSendAsyncFailed(0);

        Scanner scanner = new Scanner(System.in);
        while (true){
        System.out.println("请输入消息内容");
        String message = scanner.next();
        // 创建消息，并指定Topic，Tag和消息体
            final int index = new Random().nextInt(1000);
            Message msg = new Message(
                    "TopicTest",
                    "TagA" ,
                    "OrderId"+ index,
                    message.getBytes(RemotingHelper.DEFAULT_CHARSET)
            );
            // 发送消息到一个Broker,SendCallback接收异步返回结果的回调
            producer.send(msg, new SendCallback() {
                public void onSuccess(SendResult sendResult) {
                    System.out.printf("%-10d OK %s %n", index,
                            sendResult.getMsgId());
                }

                public void onException(Throwable e) {
                    e.printStackTrace();
                }
            });

        }
    }


}
