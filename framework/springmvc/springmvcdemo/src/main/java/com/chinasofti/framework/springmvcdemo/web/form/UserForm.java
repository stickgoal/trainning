package com.chinasofti.framework.springmvcdemo.web.form;

public class UserForm {
	
	private String username;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String toString() {
		return "UserForm [username=" + username + "]";
	}

}
