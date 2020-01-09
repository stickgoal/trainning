package me.maiz.trainning.framework.publishsubscribe;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static me.maiz.trainning.framework.publishsubscribe.LogEmitter.EXCHANGE_NAME;

public class LogConsumer {

    public void consume(int id) throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        //声明交换器
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
        //随机产生队列名
        String queueName = channel.queueDeclare().getQueue();
        //将该队列绑定到信道
        channel.queueBind(queueName, EXCHANGE_NAME, "");
        //等待消息
        System.out.println(id+" [*] Waiting for messages. To exit press CTRL+C");
        //发送回调处理
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(id+" [x] Received '" + message + "'");
        };
        //开始消费
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });

    }



}
