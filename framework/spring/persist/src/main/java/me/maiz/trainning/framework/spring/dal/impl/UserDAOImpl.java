package me.maiz.trainning.framework.spring.dal.impl;

import me.maiz.trainning.framework.spring.dal.UserDAO;
import me.maiz.trainning.framework.spring.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by Lucas on 2018-03-09.
 */
//@Repository("userDAO")
public class UserDAOImpl implements UserDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void save(User user) {
        jdbcTemplate.update("insert into sp_user(user_id,username) values(?,?)",user.getUserId(),user.getName());
    }
}
