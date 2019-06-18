package me.maiz.trainning.framework.spring.dao.impl;



import me.maiz.trainning.framework.spring.dao.UserDAO;
import me.maiz.trainning.framework.spring.dao.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository("userDAO")
public class UserDAOImpl implements UserDAO {

    @Value("123")
    private String age;

    private Map<String,Object> data;

    @Override
    public User findByUsernameAndPassword(String username, String password) {
        System.out.println("注入的age值为："+age);
        System.out.println("注入的data值为："+data);



        return new User(username);
    }

    @Override
    public List<User> findAll() {

        return new ArrayList<>();
    }

}
