package com.chinasofti.framework.springmvcdemo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chinasofti.framework.springmvcdemo.dao.UserDAO;
import com.chinasofti.framework.springmvcdemo.dao.model.User;

@Service("userService")
public class UserServiceImpl implements UserService {

	@Autowired
	private UserDAO userDAO;
	
	@Override
	public List<User> findByUserName(String username) {
		return userDAO.findByUsername(username);
	}

}
