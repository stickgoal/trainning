package me.maiz.trainning.boot.bootmq.controller;

import lombok.Data;

import java.util.Date;

@Data
public class Order {

    private int orderId;

    private String orderNo;

    private Date createDate;



}
