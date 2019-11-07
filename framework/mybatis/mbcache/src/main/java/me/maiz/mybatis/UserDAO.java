package me.maiz.mybatis;


import me.maiz.mybatis.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserDAO {

    User findById(int userId);

    User findByUsernameAndPassword(@Param("username") String username, @Param("password") String password);

    List<User> findAll();

    void save(User user);

    void delete(int id);

    void update(User user);

    Integer  countAll();


}
