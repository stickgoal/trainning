package me.maiz.trainning.framework.spring.xml.impl;

import me.maiz.trainning.framework.spring.xml.User;
import me.maiz.trainning.framework.spring.xml.UserDAO;

/**
 * Created by Lucas on 2017-01-10.
 */
public class UserDAOImpl implements UserDAO {

    private String username;

    public User findById(int id) {
        User u =new User();
        u.setUserId(id);
        u.setUsername(username);
        return  u;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
