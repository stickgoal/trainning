package com.chinasofti.framework.springmvcdemo.service;

import java.util.List;

import com.chinasofti.framework.springmvcdemo.dao.model.User;

public interface UserService {

	public List<User> findByUserName(String username);
	
	
}
