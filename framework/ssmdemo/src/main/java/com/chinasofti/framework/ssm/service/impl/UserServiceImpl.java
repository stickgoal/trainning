package com.chinasofti.framework.ssm.service.impl;

import com.chinasofti.framework.ssm.dao.UserDAO;
import com.chinasofti.framework.ssm.dao.model.User;
import com.chinasofti.framework.ssm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDAO userDAO;

    @Override
    public User login(String username, String password) {
        User user = userDAO.findByUsernameAndPassword(username, password);
        if(user==null){
            throw new RuntimeException("USERNAME_OR_PASSWORD_INCORRECT");
        }
        return user;
    }
}
