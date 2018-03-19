package me.maiz.trainning.framework.mybatis.dal.model;

import java.util.Date;

/**
 * Created by Lucas on 2018-03-19.
 */
public class User {

    private int userId;

    private String username;

    private String password;

    private int age;

    private Date birthday;


    public User() {
    }

    public User(int userId, String username, String password, int age, Date birthday) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.age = age;
        this.birthday = birthday;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", age=" + age +
                ", birthday=" + birthday +
                '}';
    }
}
