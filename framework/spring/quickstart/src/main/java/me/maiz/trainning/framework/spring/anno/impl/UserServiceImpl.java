package me.maiz.trainning.framework.spring.anno.impl;

import me.maiz.trainning.framework.spring.anno.UserDAO;
import me.maiz.trainning.framework.spring.anno.UserService;
import me.maiz.trainning.framework.spring.xml.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Lucas on 2017-01-10.
 */
@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDAO userDAO;

    public User findById(int userId) {
        return userDAO.findById(userId);
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }
}
