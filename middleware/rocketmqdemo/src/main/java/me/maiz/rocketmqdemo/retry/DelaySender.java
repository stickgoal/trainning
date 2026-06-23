package me.maiz.rocketmqdemo.retry;


import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;

public class DelaySender {
    public static void main(String[] args) throws Exception {
        // 实例化一个生产者来产生延时消息
        DefaultMQProducer producer = new DefaultMQProducer("ExampleProducerGroup");
        producer.setNamesrvAddr("localhost:9876");
        // 启动生产者
        producer.start();
        send(producer,"success");
        send(producer,"fail");

        // 关闭生产者
        producer.shutdown();
    }

    private static void send(DefaultMQProducer producer,String content) throws MQClientException, RemotingException, MQBrokerException, InterruptedException, UnsupportedEncodingException {
        Message message = new Message("TestTopic", content.getBytes("utf-8"));
        // 发送消息
        producer.send(message);
        System.out.println(LocalDateTime.now()+"发送完毕");
    }


}
