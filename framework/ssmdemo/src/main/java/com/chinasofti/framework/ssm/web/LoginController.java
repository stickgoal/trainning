package com.chinasofti.framework.ssm.web;

import com.chinasofti.framework.ssm.dao.UserDAO;
import com.chinasofti.framework.ssm.dao.model.User;
import com.chinasofti.framework.ssm.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
public class LoginController {

    private Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @RequestMapping(value = "login",method = RequestMethod.POST)
    @ResponseBody
    public String login(String username, String password, HttpSession session){
        logger.info("请求登录 {},{}",username,password);
        String message = "Y";
        try {
            User user = userService.login(username, password);
            session.setAttribute("USER_INFO_SK",user);
        }catch (RuntimeException e){
            if(e.getMessage().equals("USERNAME_OR_PASSWORD_INCORRECT")) {
                logger.info("用户账号（{}）或密码错误",username);
            }
            message="N";
        }

        return message;
    }

}
