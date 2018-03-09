package me.maiz.trainning.framework.spring.injection.service.impl;

import me.maiz.trainning.framework.spring.injection.dal.UserDAO;
import me.maiz.trainning.framework.spring.injection.model.User;
import me.maiz.trainning.framework.spring.injection.service.UserService;

/**
 * Created by Lucas on 2018-03-09.
 */
public class UserServiceImpl implements UserService {

    private UserDAO userDAO;

    public User findByName(String name) {
        System.out.println("1>>UserServiceImpl#findByName");

        return userDAO.findByName(name);
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }
}
