package com.woniuxy.boot.ssmboot.controller;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DestoryEarthController {

    @RequestMapping("dismiss")
    @ResponseBody
    @RequiresPermissions("dismiss")
    public String dismiss(){
        return "BOOM~~~~";
    }

}
