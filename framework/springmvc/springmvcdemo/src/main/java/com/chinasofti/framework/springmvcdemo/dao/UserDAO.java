package com.chinasofti.framework.springmvcdemo.dao;

import java.util.List;

import com.chinasofti.framework.springmvcdemo.dao.model.User;

public interface UserDAO {

	List<User> findByUsername(String username);
	
	
}
