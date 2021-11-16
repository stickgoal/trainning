package me.maiz.shiro.shirojwt.controller;

import lombok.extern.slf4j.Slf4j;
import me.maiz.shiro.shirojwt.common.JwtUtils;
import me.maiz.shiro.shirojwt.common.Result;
import me.maiz.shiro.shirojwt.model.User;
import me.maiz.shiro.shirojwt.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@CrossOrigin("*")
@Slf4j
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("login")
    public Result login(HttpServletResponse response, String username, String password){
        //执行登录操作，签发jwt ,但不走shiro
        log.info("登录操作：{},{}",username,password);
        User user = loginService.login(username, password);

        String jwt = JwtUtils.createJWT(user);
        response.setHeader(JwtUtils.AUTH_TOKEN_NAME,jwt);

        return Result.success(jwt);
    }


}
