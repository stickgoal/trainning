package me.maiz.trainning.framework.publishsubscribe;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class LogEmitter {

    public static final String EXCHANGE_NAME = "logs";

    public static void main(String[] args) throws IOException, TimeoutException {

        ConnectionFactory f = new ConnectionFactory();
        f.setHost("localhost");
        Connection conn = f.newConnection();
        Channel channel = conn.createChannel();
        //声明交换器
        channel.exchangeDeclare("logs", BuiltinExchangeType.FANOUT);

        String message = "[info] publishing logs";
        //发布消息
        channel.basicPublish("logs", "", null, message.getBytes("UTF-8"));

        System.out.println(" [x] Sent '" + message + "'");

    }


}
