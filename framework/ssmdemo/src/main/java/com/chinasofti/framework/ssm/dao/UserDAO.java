package com.chinasofti.framework.ssm.dao;

import com.chinasofti.framework.ssm.dao.model.User;
import org.apache.ibatis.annotations.Param;

public interface UserDAO {

    User findById(int userId);

    User findByUsernameAndPassword(@Param("username") String username, @Param("password")String password);

}
