package me.maiz.demo.websocketdemo.websocket;


import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import me.maiz.demo.websocketdemo.websocket.model.OfferPriceMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Controller
@Slf4j
public class AuctionController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    @MessageMapping("/offer")
//    @SendTo("/topic/broadcast")
    public void processMessageFromClient(@Payload OfferPriceMessage message, Principal principal) throws Exception {
        log.info("message：{}，principal:{}",message,principal);
        //TODO 判断
        if(message.getPrice()<100){
            HashMap<String, Object> result = new HashMap<>();
            result.put("success",false);
            result.put("code","NOT_MAX");
            result.put("message","您的出价"+message.getPrice()+"已不是最高价");

            messagingTemplate.convertAndSend("/queue/reply-user"+message.getJwt(),result);
//            return null;
        }else {

            messagingTemplate.convertAndSend("/topic/broadcast", message);
        }
//        return message;
    }



    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public String handleException(Throwable exception) {
        return exception.getMessage();
    }

}
