package me.maiz.framework.mvc.interceptordemo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
public class HelloController {


    @GetMapping("/hello")
    public String hello(){
        return "Hello";
    }

    @GetMapping("/login")
    public String login(HttpSession session){
        session.setAttribute("username","zhangsan");
        return "Logined";
    }

}
