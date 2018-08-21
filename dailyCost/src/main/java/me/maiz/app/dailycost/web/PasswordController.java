package me.maiz.app.dailycost.web;

import me.maiz.app.dailycost.common.MailUtil;
import me.maiz.app.dailycost.service.MailService;
import org.hibernate.validator.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("password")
public class PasswordController {

    @Autowired
    private MailService mailService;


    @RequestMapping("sendCode")
    @ResponseBody
    public String sendCode( String username){

        mailService.sendResetPasswordMail(username);
        return "success";
    }




}
