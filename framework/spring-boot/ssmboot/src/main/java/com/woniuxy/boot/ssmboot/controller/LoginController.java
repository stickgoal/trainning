package com.woniuxy.boot.ssmboot.controller;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.ShiroException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;

@Controller
public class LoginController {

    private Logger logger = LoggerFactory.getLogger(LoginController.class);

    @RequestMapping(value = "login", method = RequestMethod.GET)
    public String toLogin() {
        return "index";
    }

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public String login(String username, String password, String rememberMe, ModelMap modelMap, HttpSession session) {
        logger.info("登录请求");
        Subject subject = SecurityUtils.getSubject();
        try {
            UsernamePasswordToken token = new UsernamePasswordToken(username, password);
            token.setRememberMe("Y".equals(rememberMe));
            subject.login(token);
            session.setAttribute("user",subject.getPrincipal());
        } catch (ShiroException e) {
            logger.info("登录异常", e);
            String message = null;
            if (e instanceof UnknownAccountException) {
                message = "用户不存在";
            } else if (e instanceof IncorrectCredentialsException) {
                message = "密码不正确";
            } else {
                message = e.getMessage();
            }
            modelMap.put("msg", message);

            return "index";
        } catch (Exception e) {
            logger.info("登录异常", e);
            modelMap.put("msg", "系统出现问题");
            return "index";
        }
        return "redirect:/home";
    }

    @RequestMapping("/home")
    public String toHome(ModelMap modelMap){
        ImmutableMap<String, String> menu = ImmutableMap.of("menuName", "解散公司", "menuPerm", "dismiss");
        modelMap.put("menu",menu);
        return "home";
    }


    @RequestMapping("/logout")
    public String logout(){
         SecurityUtils.getSubject().logout();
         return "index";
    }

}
