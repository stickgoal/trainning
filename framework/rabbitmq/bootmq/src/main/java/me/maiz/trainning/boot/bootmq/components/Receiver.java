package me.maiz.trainning.boot.bootmq.components;

import org.springframework.stereotype.Component;

@Component
public class Receiver {


    public void recieve(String message){
        System.out.println("[x] 收到消息，"+message);
    }


}
