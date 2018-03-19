package me.maiz.trainning.framework.mybatis.dal.mapper;

import me.maiz.trainning.framework.mybatis.dal.model.User;

import java.util.List;
import java.util.Map;

/**
 * Created by Lucas on 2018-03-19.
 */
public interface UserMapper {

    User findByUserId(int userId);

    void insertUser(User user);

    void updateUser(User user);

    void deleteUser(int userId);

    List<User> findAll();

    List<User> findByMap(Map<String,Object> params);

    Map<String,Object> findResultMap();

    void updateUserById(User user);

    void updateUserByUsername(User user);

    List<User> findUserWithUsernameLike(Map<String,Object> params);

}
