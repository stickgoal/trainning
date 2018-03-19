package com.chinasofti.framework.springmvcdemo.web.form;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

public class RegForm {
	
	@NotBlank(message="用户名不能为空")
	@Length(max=128,min=6,message="用户名长度应该在{min}到{max}之间")
	@Email(message="用户名为邮箱")
	private String username;
	
	@NotBlank(message="密码不能为空")
	@Length(max=50,min=6,message="密码长度应该在{min}到{max}之间")
	private String password;
	
	private int age;
	
	@NotBlank(message="验证码不能为空")
	@Length(max=6,min=6,message="验证码格式不正确")
	private String captcha;


	public String getCaptcha() {
		return captcha;
	}


	public void setCaptcha(String captcha) {
		this.captcha = captcha;
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


	public int getAge() {
		return age;
	}


	public void setAge(int age) {
		this.age = age;
	}


	@Override
	public String toString() {
		return "RegForm [username=" + username + ", password=" + password + ", age=" + age + ", captcha=" + captcha
				+ "]";
	}
	
}
