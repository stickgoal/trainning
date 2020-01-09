package me.maiz.trainning.boot.bootmq.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

@RestController
@RequestMapping("order")
public class OrderCreateController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping("create")
    public String create() throws JsonProcessingException {
        for (int i=0;i<100;i++) {
            Order order = new Order();
            order.setOrderId(new Random().nextInt());
            order.setOrderNo("202019101901231bc");
            order.setCreateDate(new Date());
            //转json字符串
            objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"));
            String jsonOrder = objectMapper.writeValueAsString(order);

            rabbitTemplate.convertAndSend("order.delivery.queue", jsonOrder);
        }
        return "true";
    }

}
