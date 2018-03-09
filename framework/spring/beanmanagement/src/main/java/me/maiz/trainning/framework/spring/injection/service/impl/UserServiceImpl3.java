package me.maiz.trainning.framework.spring.injection.service.impl;

import me.maiz.trainning.framework.spring.injection.dal.UserDAO;
import me.maiz.trainning.framework.spring.injection.model.User;
import me.maiz.trainning.framework.spring.injection.service.UserService;

/**
 * Created by Lucas on 2018-03-09.
 */
public class UserServiceImpl3 implements UserService {

    private UserDAO userDAO;


    public User findByName(String name) {
        System.out.println("3>>>>>>>>>>>>>>UserServiceImpl3#findByName");
        return userDAO.findByName(name);
    }

    public static UserServiceImpl3 make(UserDAO ua){
        UserServiceImpl3 userServiceImpl3 = new UserServiceImpl3();
        userServiceImpl3.userDAO=ua;
        return userServiceImpl3;
    }


}
