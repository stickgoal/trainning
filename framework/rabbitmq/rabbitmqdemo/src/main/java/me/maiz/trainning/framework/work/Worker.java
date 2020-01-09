package me.maiz.trainning.framework.work;

import com.rabbitmq.client.*;
import me.maiz.trainning.framework.simple.Sender;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Worker {

    public void process(int id){
        ConnectionFactory f = new ConnectionFactory();
        f.setHost("localhost");

        try {
            Connection connection = f.newConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare(NewTask.QUEUE_NAME,false,false,false,null);
            System.out.println(id+" [*] Waiting for messages. To exit press CTRL+C");

            //信息到达的回调
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println(id+" [x] Received '" + message + "'");

                //业务处理
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(" [x] Done ");
            };

            CancelCallback emptyCallback = consumerTag -> {};
            boolean autoAck = true;
            channel.basicConsume(NewTask.QUEUE_NAME, autoAck,deliverCallback, emptyCallback);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

}
