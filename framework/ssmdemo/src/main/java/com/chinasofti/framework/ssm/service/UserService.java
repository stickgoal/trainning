package com.chinasofti.framework.ssm.service;

import com.chinasofti.framework.ssm.dao.model.User;

public interface UserService {

    User login(String username,String password);


}
