package me.maiz.trainning.framework.simple;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Reciever {

    public static void main(String[] args) {
        ConnectionFactory f = new ConnectionFactory();
        f.setHost("localhost");

        try {
            Connection connection = f.newConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare(Sender.QUEUE_NAME,false,false,false,null);
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

            DeliverCallback deliverCallback = new DeliverCallback() {
                @Override
                public void handle(String consumerTag, Delivery delivery) throws IOException {
                    String message = new String(delivery.getBody(), "UTF-8");
                    System.out.println(" [x] Received '" + message + "'");
                }
            };

            CancelCallback emptyCallback = new CancelCallback() {
                @Override
                public void handle(String consumerTag) throws IOException {

                }
            };
            channel.basicConsume(Sender.QUEUE_NAME, deliverCallback, emptyCallback);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

}
