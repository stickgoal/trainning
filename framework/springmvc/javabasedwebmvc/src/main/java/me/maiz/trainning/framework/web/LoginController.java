package me.maiz.trainning.framework.web;


import me.maiz.trainning.framework.dao.UserDAO;
import me.maiz.trainning.framework.dao.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoginController {


    @Autowired
    private UserDAO userDAO;

    @RequestMapping(value = "login",method = RequestMethod.GET)
    @ResponseBody
    public User toLogin(){
        User byId = userDAO.findById(5);
        return byId;
    }

}
