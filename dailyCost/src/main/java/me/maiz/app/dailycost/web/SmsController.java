package me.maiz.app.dailycost.web;

import com.avos.avoscloud.AVOSCloud;
import me.maiz.app.dailycost.common.MessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SmsController {

    @Autowired
    private MessageSender messageSender;

    @RequestMapping("sms")
    public String send(String msg,String mobile){
        messageSender.sendMessage("15523836745");

        return "success";
    }

}
