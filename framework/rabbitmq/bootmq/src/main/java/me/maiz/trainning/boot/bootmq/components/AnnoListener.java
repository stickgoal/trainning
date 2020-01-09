package me.maiz.trainning.boot.bootmq.components;

import me.maiz.trainning.boot.bootmq.config.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class AnnoListener {

//    @RabbitListener(queues=RabbitConfig.QUEUE_NAME)
    public void listen(String message){
        System.out.println("[x] 收到消息"+message);
    }

}
