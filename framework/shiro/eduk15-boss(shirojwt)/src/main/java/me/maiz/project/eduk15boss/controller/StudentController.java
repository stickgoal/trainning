package me.maiz.project.eduk15boss.controller;

import lombok.extern.slf4j.Slf4j;
import me.maiz.project.eduk15boss.common.Result;
import me.maiz.project.eduk15boss.dao.UserMapper;
import me.maiz.project.eduk15boss.dao.UserMapperCustom;
import me.maiz.project.eduk15boss.model.User;
import me.maiz.project.eduk15boss.model.UserExample;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class StudentController {

    @Autowired
    private UserMapperCustom userMapper;

    @GetMapping("students")
    @RequiresPermissions("students:query")
    public Result findStudent(String name,
                              @RequestParam(defaultValue = "1") int pageIndex,
                              @RequestParam(defaultValue = "10")int pageSize){
        log.info("查询学生{},{},{}",name,pageIndex,pageSize);
        List<User> userList = userMapper.findByNamePage(name, (pageIndex - 1) * pageSize, pageSize);        return Result.success(userList);
    }

    @PostMapping("student/add")
    @RequiresPermissions("student:add")
    public Result findStudent(){
        log.info("添加学生");
        return Result.success(123);
    }


}
