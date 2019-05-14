package me.maiz.trainning.framework.spring.web;

import me.maiz.trainning.framework.spring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller("userController")
public class UserController {

    @Autowired
    private UserService userService;


    public String login(String username,String password){

       String resultMessage =  userService.login(username,password);
        return resultMessage;
    }


    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
