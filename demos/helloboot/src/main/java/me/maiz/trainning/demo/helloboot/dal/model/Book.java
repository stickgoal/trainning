package me.maiz.trainning.demo.helloboot.dal.model;

import lombok.Data;

import java.util.Date;

@Data
public class Book {

    private int id;

    private String bookName;

    private String status;

    private Date createDate;

    private Date upTime;

    private double price;

}
