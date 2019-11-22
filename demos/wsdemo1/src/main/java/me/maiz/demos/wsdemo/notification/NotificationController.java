package me.maiz.demos.wsdemo.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;

@Controller
@Slf4j
public class NotificationController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @RequestMapping("doSend")
    @ResponseBody
    public  String doSend(){

        //发送消息
        messagingTemplate.convertAndSend("/queue/notification","hello"+Math.random());

        return "success";
    }

}
