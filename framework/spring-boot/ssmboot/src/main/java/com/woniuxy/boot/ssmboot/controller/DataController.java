package com.woniuxy.boot.ssmboot.controller;

import com.woniuxy.boot.ssmboot.controller.form.UserForm;
import com.woniuxy.boot.ssmboot.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;
import java.util.Date;

@Controller
public class DataController {

    @GetMapping("toIndex")
    public String toIndex(int userId,UserForm userForm, ModelMap modelMap){
        User user = new User();
        user.setUserId(1);
        user.setUsername("123");
        user.setPassword("^&*()");

        modelMap.put("user",user);
        modelMap.put("username","wanger");
        modelMap.put("downloadUrl","http://baidu.com");

        modelMap.put("usernameClass","hot");

        modelMap.put("age",13);

        modelMap.put("dishes", Arrays.asList("盘龙茄子","红烧排骨","苦瓜炒肉"));
//        modelMap.put("users", Arrays.asList(new User(1,"张三","*****"),new User(2,"李四","*****"),new User(3,"王五","*****")));

        modelMap.put("myDate",new Date());

        return "index1";
    }

}
