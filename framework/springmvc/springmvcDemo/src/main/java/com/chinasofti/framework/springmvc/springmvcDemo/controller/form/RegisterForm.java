package com.chinasofti.framework.springmvc.springmvcDemo.controller.form;

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.chinasofti.framework.springmvc.springmvcDemo.enums.GenderEnum;

public class RegisterForm {

	@NotNull
	@Length(max=10,min=2,message="长度合法，请在${max}和${min}")
	private String username;

	private int age;

	private Date birthday;

	private GenderEnum gender;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public GenderEnum getGender() {
		return gender;
	}

	public void setGender(GenderEnum gender) {
		this.gender = gender;
	}

	@Override
	public String toString() {
		return "RegisterForm [username=" + username + ", age=" + age + ", birthday=" + birthday + ", gender=" + gender
				+ "]";
	}

}
