package com.woniuxy.mvc26test.controller;

import java.util.Date;
import java.util.List;

public class User {
	private String username;
	
	private String password;
	
	private List<String> hobbies;
	
	private Date birthday;
	
	private int age;
	
	private Address address;
	
	public User() {
	}
	
	public User(String username, String password, List<String> hobbies, Date birthday, int age, Address address) {
		super();
		this.username = username;
		this.password = password;
		this.hobbies = hobbies;
		this.birthday = birthday;
		this.age = age;
		this.setAddress(address);
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public List<String> getHobbies() {
		return hobbies;
	}
	public void setHobbies(List<String> hobbies) {
		this.hobbies = hobbies;
	}
	public Date getBirthday() {
		return birthday;
	}
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	@Override
	public String toString() {
		return "User [username=" + username + ", password=" + password + ", hobbies=" + hobbies + ", birthday="
				+ birthday + ", age=" + age + "]";
	}
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}

	
	
}
