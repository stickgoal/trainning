package me.maiz.trainning.framework.spring.anno.impl;

import me.maiz.trainning.framework.spring.anno.UserDAO;
import me.maiz.trainning.framework.spring.xml.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

/**
 * Created by Lucas on 2017-01-10.
 */
@Repository("userDAO")
public class UserDAOImpl implements UserDAO {

    @Value("wanger")
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
