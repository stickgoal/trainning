package com.example.releasedemo.controller;

import com.example.releasedemo.service.HelloService;
import com.example.releasedemo.service.impl.HelloServiceImpl;
import com.example.releasedemo.threadloadDemo.ThreadLocalHolder;

public class HelloController {

    private HelloService hs = new HelloServiceImpl();

    public void doSth(){
        ThreadLocalHolder.set("hello,my service~~~");
        hs.doSth();
    }

    public static void main(String[] args) {
        new HelloController().doSth();
    }

}
