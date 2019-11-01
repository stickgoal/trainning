package me.maiz.spring.service.impl;

import me.maiz.spring.service.User;
import me.maiz.spring.service.UserService;

public class UserServiceImpl implements UserService {
    @Override
    public void addUser(User user) {
        System.out.println("执行addUser");
    }
}
