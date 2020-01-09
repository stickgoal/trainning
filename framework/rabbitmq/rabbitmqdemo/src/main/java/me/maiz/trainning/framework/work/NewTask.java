package me.maiz.trainning.framework.work;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class NewTask {
    public static final String QUEUE_NAME="Work";

    public static void main(String[] args) {
        ConnectionFactory f = new ConnectionFactory();
        f.setHost("localhost");
        try {
            //创建链接
            Connection connection = f.newConnection();
            //创建信道
            Channel channel = connection.createChannel();
            //创建一个队列
            /*
                queue – 队列名
                durable – 是否持久化，如果为true则会持久化 (服务器重启后仍存在)
                exclusive – 是否独占，true则为独占队列，其他链接不能使用
                autoDelete – 是否自动删除消息，true则在使用完后删除
                arguments – map类型，其他参数
            * */
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            String message = String.join(" ",args);


            //发布消息
            /**
             * exchange – 发布到的交换器
             * routingKey – 路由键，这里近似的看做队列名
             * props – 其他属性
             * body – 消息体
             */
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            System.out.println(" [x] Sent '" + message + "'");

            //关闭链接
//            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
