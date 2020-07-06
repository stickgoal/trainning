package com.woniuxy.redisdemo.redisbootdemo34;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
public class RedisTestController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping("test")
    public String test(String k,String v){

        ValueOperations opsForValue = redisTemplate.opsForValue();

        opsForValue.set(k,v);

        BoundValueOperations<String, String> boundValueOps = redisTemplate.boundValueOps(k);
        //setIfAbsent = NX   setIfPresent = XX
        Boolean aBoolean = boundValueOps.setIfPresent(v, 60, TimeUnit.SECONDS);
        System.out.println(aBoolean);
        Long result = boundValueOps.increment(10L);

        System.out.println(result);

        return "success";
    }

    @GetMapping(value="random",produces = "text/html;charset=utf-8")
    public String random(){

        BoundSetOperations<String, String> onDuty = redisTemplate.boundSetOps("onDuty");
        onDuty.add("王子明","焦竟键","胡国栋","伍沛霖","刘飞","温礼亮","张可伟","邓龙波","赵进林","腾臣","郑施利","应毓达","蒲超越","陈克金","丁秋辰","窦梓铭","屈晓东","潘百宽","温上玮","邓美婷","王翰宇","黎凯瑞","王渝怀","侯中平","皮洪宇","许超","黄安","何智航","罗虎","范家伟","邓皓月","朱小波","陆月","曹力引","罗伟","唐凡为");

        return "<html><head><title>The Winner</title><meta charset='utf-8'></head><body><h1>Lucky Guy："+onDuty.pop()+"</h1></body></html>";
    }

}
