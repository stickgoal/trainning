package com.woniuxy.boot.ssmboot.controller;


import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Lucas
 * @since 2019-11-12
 */
@RestController
@RequestMapping("/ssmboot/pig")
public class PigController {

//    @RequiresRoles("president")
    @RequiresPermissions("dismiss")
    @GetMapping("getData")
    public String getData(){
        return "one pink pig";
    }

}

