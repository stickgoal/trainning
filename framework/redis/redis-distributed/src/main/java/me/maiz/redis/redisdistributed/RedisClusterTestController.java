package me.maiz.redis.redisdistributed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedisClusterTestController {

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping("test")
    public String test(){
        System.out.println("123");

        redisTemplate.opsForValue().set("hello","TestFromBoot"+System.currentTimeMillis());

        return (String) redisTemplate.opsForValue().get("hello");
    }


}
