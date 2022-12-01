package me.maiz.demo.websocketdemo.websocket;


import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import me.maiz.demo.websocketdemo.common.Result;
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
    public void processMessageFromClient(@Payload OfferPriceMessage message, Principal principal) throws Exception {
        log.info("message：{}，principal:{}",message,principal);
        //判断是否大于目前最高价
        double currentMaxPrice = getCurrentMax(message.getAuctionId());
        if(message.getPrice()<=currentMaxPrice){
            //不是最高价，向用户发送出价失败消息
            Result<String> result = Result.getInstance(String.class).setCode("NOT_MAX").setData("不是最高价");

            messagingTemplate.convertAndSend("/queue/reply-user"+message.getJwt(),result);
        }else {
            //是最高价，广播给所有人
            messagingTemplate.convertAndSend("/topic/broadcast", message);
        }
    }

    private double getCurrentMax(int auctionId) {
        //TODO 查数据库或者redis
        return 100;
    }


    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public String handleException(Throwable exception) {
        return exception.getMessage();
    }

}
