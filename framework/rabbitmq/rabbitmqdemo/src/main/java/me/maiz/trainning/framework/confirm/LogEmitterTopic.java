package me.maiz.trainning.framework.confirm;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class LogEmitterTopic {

    public static final String EXCHANGE_NAME = "topic_logs";

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {

        ConnectionFactory f = new ConnectionFactory();
        f.setHost("localhost");
        Connection conn = f.newConnection();
        Channel channel = conn.createChannel();
        //启动接受确认机制
        channel.confirmSelect();

        //声明交换器
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);


        channel.addConfirmListener(new ConfirmListener() {
            @Override
            public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                System.out.println("信息["+deliveryTag+"]已被接受+++");
            }

            @Override
            public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                System.out.println("信息["+deliveryTag+"]未被接受---");
            }
        });

        Thread.sleep(100);
        //发布消息
        send(channel,"快速的橘黄色兔子","quick.orange.rabbit");

//        publishIndividually(channel);


        send(channel,"懒橘黄大象","lazy.orange.elephant");
        send(channel,"懒橘黄色的狐狸","lazy.orange.fox");
        send(channel,"快速的棕色的狐狸","quick.brown.fox");


    }

    private static void publishIndividually(Channel channel) throws IOException, InterruptedException, TimeoutException {
        //接受确认通知，阻塞等待5000毫秒，若无结果则抛出异常
        System.out.println("阻塞等待消息接受通知 开始于："+System.currentTimeMillis());
        channel.waitForConfirmsOrDie(5000);
        System.out.println("阻塞等待消息接受通知 结束于："+System.currentTimeMillis());
    }

    private static void send(Channel channel,String message,String routingKey) throws IOException {
        channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes("UTF-8"));
        System.out.println(" [x] Sent '" + message + "' with key '"+routingKey+"'");

    }
}
