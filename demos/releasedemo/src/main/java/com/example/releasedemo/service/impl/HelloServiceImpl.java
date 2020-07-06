package com.example.releasedemo.service.impl;

import com.example.releasedemo.service.HelloService;
import com.example.releasedemo.threadloadDemo.ThreadLocalHolder;

public class HelloServiceImpl implements HelloService {
    @Override
    public void doSth() {
        System.out.println(ThreadLocalHolder.get());
    }
}
