package me.maiz.demo.jsonlogin.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import lombok.extern.slf4j.Slf4j;
import me.maiz.demo.jsonlogin.securitycomponents.JwtHelper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class ApiController {

    @Resource
    private JwtHelper jwtHelper;

    @GetMapping("/api/test")
    public String doIt(){
        return "success";
    }

    @GetMapping("/getToken")
    public String getToken(String username,String password){
        Date date = DateTime.now().offset(DateField.MINUTE, 60).toJdkDate();

        Map<String, Object> payload = MapUtil.of("username",username);
        return jwtHelper.createJWT(date,payload);
    }
}
