package me.maiz.trainning.framework.spring.injection.dal.impl;

import me.maiz.trainning.framework.spring.injection.dal.UserDAO;
import me.maiz.trainning.framework.spring.injection.model.User;

/**
 * Created by Lucas on 2018-03-09.
 */
public class UserDAOImpl implements UserDAO {

    public User findByName(String name) {
        return new User();
    }
}
