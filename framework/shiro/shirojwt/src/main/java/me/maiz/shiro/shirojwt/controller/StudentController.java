package me.maiz.shiro.shirojwt.controller;

import me.maiz.shiro.shirojwt.common.Result;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StudentController {

    @GetMapping("getStu")
    public Result getStudent(){

        return Result.success();
    }

    @RequiresPermissions("hello")
    @GetMapping("getStu2")
    public Result getStudent2(){

        return Result.success();
    }


}
