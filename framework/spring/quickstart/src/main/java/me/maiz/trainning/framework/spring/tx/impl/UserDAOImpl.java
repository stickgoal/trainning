package me.maiz.trainning.framework.spring.tx.impl;

import me.maiz.trainning.framework.spring.tx.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserDAOImpl implements UserDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void insert(String username){

        jdbcTemplate.update("insert into user values(default,?)",username);

    }

    public void update(int userId,String username){

        jdbcTemplate.update("update  user  set username=? where user_id=?",username,userId);

    }


}
