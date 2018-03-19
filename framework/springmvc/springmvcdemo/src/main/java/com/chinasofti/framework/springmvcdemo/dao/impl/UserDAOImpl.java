package com.chinasofti.framework.springmvcdemo.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.chinasofti.framework.springmvcdemo.dao.UserDAO;
import com.chinasofti.framework.springmvcdemo.dao.model.User;

@Repository("userDAO")
public class UserDAOImpl implements UserDAO{
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	

	@Override
	public List<User> findByUsername(String username) {
		
		List<User> u = jdbcTemplate.query("select * from user where username like concat('%',?,'%')", new Object[]{username},new RowMapper<User>(){

			@Override
			public User mapRow(ResultSet rs, int rowNum) throws SQLException {
				User u = new User();
				u.setUserId(rs.getInt(1));
				u.setUsername(rs.getString(2));
				u.setPasswd(rs.getString(3));
				u.setStatus(rs.getString(4));
				return u;
			}
			
		});
		
		return u;
	}

}
