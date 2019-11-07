package me.maiz.trainning.framework.ssmjavaconfig.web;

import me.maiz.trainning.framework.ssmjavaconfig.dao.UserMapper;
import me.maiz.trainning.framework.ssmjavaconfig.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @Autowired
    private UserMapper userMapper;

    @RequestMapping("getData")
    public User getData(int userId){
        User user = userMapper.selectByPrimaryKey(userId);
        return user;
    }

}
