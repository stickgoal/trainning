package me.maiz.app.dailycost.web;

import me.maiz.app.dailycost.common.MailUtil;
import me.maiz.app.dailycost.common.RandomUtil;
import me.maiz.app.dailycost.service.MailService;
import org.hibernate.validator.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("password")
public class PasswordController extends BaseController{

    @Autowired
    private MailService mailService;


    @RequestMapping("sendCode")
    @ResponseBody
    public String sendCode(HttpSession session, String username){

        String code = RandomUtil.randomString(4);
        mailService.sendResetPasswordMail(username, code);

        session.setAttribute("resetPasswordCaptcha",code);
        return "success";
    }




}
