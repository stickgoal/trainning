package com.woniuxy.redisdemo.redisbootdemo34;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
public class SessionTestController {

    @GetMapping("set")
    public String setSession(String key, String value, HttpSession session){
        session.setAttribute(key,value);
        return "set success";
    }

    @GetMapping("get")
    public String getSession(String key, HttpSession session){

        return session.getAttribute(key).toString();
    }

}
