package me.maiz.framework.shiro.shiroboot.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.ShiroException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {
    private Logger logger = LoggerFactory.getLogger(LoginController.class);

    @GetMapping("login")
    public String login() {
        return "index";
    }

    @PostMapping("login")
    public String login(String username, String password, ModelMap modelMap) {
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        try {
            SecurityUtils.getSubject().login(token);
        } catch (ShiroException e) {
            logger.info("登录出错", e);
            String name = e.getClass().getName();
            String message = null;
            switch (name) {
                case "UnknownAccountException":
                    message = "账户不存在";
                    break;
                case "IncorrectCredentialsException":
                    message = "密码错误";
                    break;
                default:
                    message = "登录出错:" + e.getMessage();
            }

            modelMap.put("msg", message);
            return "index";
        } catch (Exception e) {
            logger.error("登录报错", e);
            return "error";
        }
        return "home";
    }

    @RequestMapping("logout")
    public String logout() {
        SecurityUtils.getSubject().logout();
        return "index";
    }

    @RequestMapping("home")
    public String home() {
        return "home";
    }

    /**
     * 解决shiro登录成功跳转页面不正常的问题
     * @return
     */
    @RequestMapping("/")
    public String index() {
        return SecurityUtils.getSubject().isAuthenticated()?"home":"index";
    }

    @RequestMapping("unauthorized")
    public String unauthorized() {
        return "unauthorized";
    }

}
