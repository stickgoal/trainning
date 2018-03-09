package com.chinasofti.framework.dao.entity;

import java.util.Date;

/**
 * Created by Lucas on 2017-09-25.
 */
public class User {

    private int id;

    private String userId;

    private int age;

    private Date birthday;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", age=" + age +
                ", birthday=" + birthday +
                '}';
    }
}
