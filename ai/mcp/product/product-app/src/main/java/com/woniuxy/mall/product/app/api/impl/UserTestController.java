package com.woniuxy.mall.product.app.api.impl;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.woniuxy.mall.common.result.Result;
import com.woniuxy.mall.user.client.UserQueryClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserTestController {
    @Autowired
    private UserQueryClient userQueryClient;


    @GetMapping("/testUser")
    @SentinelResource(fallback = "testUserFallback")
    public Result testUser(){

        return userQueryClient.getUserById(1);
    }

    public Result testUserFallback(){
        return Result.fail("505","服务暂不可用，请稍后再试");
    }
}
