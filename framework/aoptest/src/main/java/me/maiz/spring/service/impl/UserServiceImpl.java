package me.maiz.spring.service.impl;

import me.maiz.spring.service.User;
import me.maiz.spring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("userService")
public class UserServiceImpl implements UserService {
    @Override
    public void addUser(User user) {
        if(user==null){
            throw new RuntimeException("");
        }


        System.out.println("执行addUser");
    }
}
