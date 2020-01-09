package me.maiz.trainning.framework.confirm;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static me.maiz.trainning.framework.topics.LogEmitterTopic.EXCHANGE_NAME;


public class LogConsumerTopic {

    public void consume(String id,String[] bindingKeys) throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        //声明交换器
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        //随机产生队列名
        String queueName = channel.queueDeclare().getQueue();
        //将该队列绑定到信道
        for(String bindingKey:bindingKeys) {
            channel.queueBind(queueName, EXCHANGE_NAME, bindingKey);
        }
        //等待消息
        System.out.println(id+" [*] Waiting for messages. To exit press CTRL+C");
        //消费回调处理
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(id+" [x] Received '" + message + "'");
        };
        //开始消费
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });

    }



}
