package me.maiz.trainning.framework.web;

import org.springframework.format.datetime.DateFormatter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class HelloController {

    @RequestMapping("/h")
    public String h(){
        return "abc";
    }
    @RequestMapping("/j")
    @ResponseBody
    public User getData(){
        User data= new User();
        data.setUsername("nameabc");
        data.setBirthday(new Date());
        return data;
    }
}
