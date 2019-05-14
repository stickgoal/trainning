package me.maiz.trainning.framework.spring.dao;



import me.maiz.trainning.framework.spring.dao.model.User;

import java.util.List;

public interface UserDAO {

    User findByUsernameAndPassword(String username, String password);

    List<User> findAll();
}
