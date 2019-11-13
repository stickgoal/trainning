package me.maiz.framework.shiro.shiroboot.controller;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @RequestMapping("getData")
    @RequiresRoles("hr_mgr")
    public String doSth(){
        return "data";
    }

    @RequestMapping("hello")
    @RequiresPermissions(":lu:welfare")
    public String doSth2(){
        return "hello";
    }

}
