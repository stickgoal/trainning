package me.maiz.trainning.demo.redisbootdemo1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedisController {

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("add")
    public void  add(String k,String v){
        redisTemplate.opsForSet().add(k,v);
    }

    //http://localhost:8080/addHash?k=lucas&v=hello
    @GetMapping("addHash")
    public void  addHash(String k,String v){
        redisTemplate.boundHashOps("myHash").put(k,v);
    }

    //http://localhost:8080/getHash?k=lucas   =>  hello
    @GetMapping("getHash")
    public String  getHash(String k){
      return (String) redisTemplate.boundHashOps("myHash").get(k);
    }
}
