package me.maiz.framework.boot.ssmboot.web;

import me.maiz.framework.boot.ssmboot.dal.UserMapper;
import me.maiz.framework.boot.ssmboot.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @Autowired
    private UserMapper userMapper;

    @RequestMapping("getData")
    public User getData(int userId){
       return userMapper.selectByPrimaryKey(userId);
    }

}
