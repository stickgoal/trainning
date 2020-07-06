package com.woniuxy.redisdemo.redisbootdemo34;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController

public class RedisCacheController {

    public static final String USER_CACHE_PREFIX = "appcache::user::";
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("getData")
    public Map<String,Object> getData(int userId) throws JsonProcessingException {

        String key = USER_CACHE_PREFIX + userId;
        BoundValueOperations<String, String> ops = redisTemplate.boundValueOps(key);
        //查询缓存是否有该数据
        String value = ops.get();
        if(value==null||value.length()==0){
            System.out.println("缓存中没有数据，从数据库中加载");
            //从数据库加载
            Map<String,Object> data = new HashMap<>();
            data.put("userId",userId);
            data.put("username","wanger");
            data.put("password","******");
            data.put("email","haha@hoho.com");

            //放入缓存
            ops.set(objectMapper.writeValueAsString(data),5, TimeUnit.MINUTES);
            return data;
        }else{
            System.out.println("从缓存中返回");
            //直接返回缓存数据
           return objectMapper.readValue(value,Map.class);

        }

    }

    @GetMapping("deleteUser")
    public boolean deleteUser(int userId) throws JsonProcessingException {
        String key = USER_CACHE_PREFIX + userId;
        redisTemplate.delete(key);
        //删除用户
        return true;
    }

    @GetMapping("modifyUser")
    public boolean modifyUser(int userId) throws JsonProcessingException {

        String key = USER_CACHE_PREFIX + userId;
        BoundValueOperations<String, String> ops = redisTemplate.boundValueOps(key);
        //更新数据库
        Map<String,Object> data = new HashMap<>();
        data.put("userId",userId);
        data.put("username","zhangsan");
        data.put("password","******");
        data.put("email","gaga@hoho.com");
        //更新缓存
        ops.set(objectMapper.writeValueAsString(data),5,TimeUnit.MINUTES);

        return true;
    }


}
