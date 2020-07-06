package me.maiz.project.highconcurrency.service.impl;

import me.maiz.project.highconcurrency.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisServiceImpl implements RedisService {


    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void set(String key, String value, long expireTime) {
        if(expireTime==-1){
            redisTemplate.opsForValue().set(key,value);
        }else {
            redisTemplate.opsForValue().set(key, value, expireTime);
        }
    }

    @Override
    public void setExpireDays(String key, String value, int expireDays) {
        redisTemplate.opsForValue().set(key,value,expireDays,TimeUnit.DAYS);

    }

    @Override
    public void setExpireMinutes(String key, String value, int expireMinutes) {
        redisTemplate.opsForValue().set(key,value,expireMinutes,TimeUnit.MINUTES);

    }

    @Override
    public String  get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

}
