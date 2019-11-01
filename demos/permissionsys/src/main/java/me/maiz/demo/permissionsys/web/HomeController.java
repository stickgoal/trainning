package me.maiz.demo.permissionsys.web;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    @RequestMapping("home")
    public String home(){
        Session session = SecurityUtils.getSubject().getSession();
        session.setAttribute("abc","def");
        return "home";
    }
}
