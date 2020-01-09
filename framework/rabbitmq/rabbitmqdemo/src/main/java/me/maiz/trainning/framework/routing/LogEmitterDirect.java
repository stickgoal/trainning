package me.maiz.trainning.framework.routing;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class LogEmitterDirect {

    public static final String EXCHANGE_NAME = "direct_logs";

    public static void main(String[] args) throws IOException, TimeoutException {

        ConnectionFactory f = new ConnectionFactory();
        f.setHost("localhost");
        Connection conn = f.newConnection();
        Channel channel = conn.createChannel();
        //声明交换器
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        //发布消息
        send(channel,"磁盘已满","ERROR");
        send(channel,"错误的订单号","WARN");
        send(channel,"收到用户登录请求","INFO");

    }

    private static void send(Channel channel,String message,String routingKey) throws IOException {
        channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes("UTF-8"));
        System.out.println(" [x] Sent '" + message + "' with key '"+routingKey+"'");

    }

}
