package me.maiz.trainning.demo.redisbootdemo.controller;

import lombok.extern.slf4j.Slf4j;
import me.maiz.trainning.demo.redisbootdemo.dao.UserDAO;
import me.maiz.trainning.demo.redisbootdemo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Slf4j
public class UserController {

    @Autowired
    private UserDAO userDAO;

    // value为缓存的名字
    // key需要从参数中获取,且不能为基本类型
    // unless 是排除条件，满足条件的不缓存
    @Cacheable(value = "users", key = "#userId", unless = "#result.status != 'NORMAL'")
    @GetMapping("getUser")
    public User getUser(Integer userId){
        User user = userDAO.findById(userId).get();
        log.info("查询结果"+user);
        return user;
    }

    //更新缓存
    @CachePut(value = "users",key = "#user.userId")
    @GetMapping("modifyUser")
    @Transactional
    public User modifyUser(User user){
        userDAO.updateUsername(user.getUsername(),user.getUserId());
        return userDAO.findById(user.getUserId()).get();
    }

    //删除并清理缓存
    @CacheEvict(value = "users",key = "#userId")
    @GetMapping("deleteUser")
    @Transactional
    public void deleteUser(Integer userId){
        userDAO.deleteById(userId);
    }

    //清理缓存
    @CacheEvict(value = "users",key = "#userId")
    @GetMapping("evictCache")
    public void evictCache(Integer userId){
    }

}
