package me.maiz.trainning.framework.topics;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class LogEmitterTopic {

    public static final String EXCHANGE_NAME = "topic_logs";

    public static void main(String[] args) throws IOException, TimeoutException {

        ConnectionFactory f = new ConnectionFactory();
        f.setHost("localhost");
        Connection conn = f.newConnection();
        Channel channel = conn.createChannel();
        //声明交换器
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        
        //发布消息
        send(channel, "快速的橘黄色兔子", "quick.orange.rabbit");
        send(channel, "懒橘黄大象", "lazy.orange.elephant");
        send(channel, "懒橘黄色的狐狸", "lazy.orange.fox");
        send(channel, "快速的棕色的狐狸", "quick.brown.fox");

    }

    private static void send(Channel channel, String message, String routingKey) throws IOException {
        channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes("UTF-8"));
        System.out.println(" [x] Sent '" + message + "' with key '" + routingKey + "'");

    }
}
