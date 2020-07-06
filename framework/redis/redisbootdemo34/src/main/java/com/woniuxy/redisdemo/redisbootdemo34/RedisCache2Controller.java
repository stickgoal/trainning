package com.woniuxy.redisdemo.redisbootdemo34;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(value ="redis")
public class RedisCache2Controller {


    @GetMapping("getData")
    @Cacheable(value="userCache",key = "#userId")
    public Map<String, Object> getData(int userId) throws JsonProcessingException {


        System.out.println("缓存中没有数据，从数据库中加载");
        //从数据库加载
        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("username", "wanger");
        data.put("password", "******");
        data.put("email", "haha@hoho.com");
        return data;

    }

    @GetMapping("deleteUser")
    @CacheEvict(value = "userCache",key = "#userId")
    public boolean deleteUser(int userId) throws JsonProcessingException {

        return true;
    }

    @GetMapping("modifyUser")
    @CachePut(value = "userCache",key = "#data.userId")
    public User modifyUser( User data) throws JsonProcessingException {
        System.out.println(data);
        return data;
    }


}
