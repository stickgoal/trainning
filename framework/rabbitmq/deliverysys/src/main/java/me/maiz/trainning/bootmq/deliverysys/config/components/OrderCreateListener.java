package me.maiz.trainning.bootmq.deliverysys.config.components;

import me.maiz.trainning.bootmq.deliverysys.config.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class OrderCreateListener {

    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
    public void orderCreateOperation(String orderMessage){

        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("[x] 收到订单创建消息，执行发货操作 订单信息："+orderMessage);

    }

}
