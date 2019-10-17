package me.maiz.demo;

import lombok.Data;

import java.util.Date;

@Data
public class Employment {

    private int index;

    private String studentNo;

    private String name;

    private String gender;

    private String degree;

    private String university;

    private String major;

    private Date graduateTime;

    private String company;

    private String position;

    private double salary;

    private String city;

    private Date employmentTime;

    private String memo;
}
