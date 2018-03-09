package me.maiz.trainning.framework.spring.xml.impl;

import me.maiz.trainning.framework.spring.xml.User;
import me.maiz.trainning.framework.spring.xml.UserDAO;
import me.maiz.trainning.framework.spring.xml.UserService;

/**
 * Created by Lucas on 2017-01-10.
 */
public class UserServiceImpl implements UserService {

    private UserDAO userDAO;

    public User findById(int userId) {
        return userDAO.findById(userId);
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }
}
