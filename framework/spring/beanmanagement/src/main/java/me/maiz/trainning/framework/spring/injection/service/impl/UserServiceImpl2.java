package me.maiz.trainning.framework.spring.injection.service.impl;

import me.maiz.trainning.framework.spring.injection.dal.UserDAO;
import me.maiz.trainning.framework.spring.injection.model.User;
import me.maiz.trainning.framework.spring.injection.service.UserService;

/**
 * Created by Lucas on 2018-03-09.
 */
public class UserServiceImpl2 implements UserService {

    private UserDAO userDAO;

    public UserServiceImpl2(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User findByName(String name) {
        System.out.println(">>>>>>UserServiceImpl2#findByName");
        return userDAO.findByName(name);
    }


}
