package me.maiz.demo.websocketdemo.controller;

import lombok.extern.slf4j.Slf4j;
import me.maiz.demo.websocketdemo.common.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class LoginController {

    @RequestMapping("login")
    public Result<String> login(String username){
        log.info("username:{}",username);
        return Result.getInstance(String.class).setCode(200).setData(username);
    }

}
