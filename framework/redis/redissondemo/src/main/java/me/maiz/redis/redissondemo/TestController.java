package me.maiz.redis.redissondemo;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
public class TestController {

    @Autowired
    private RedissonClient redissonClient;

    @GetMapping("lock")
    public String lock(){
        RLock rLock = redissonClient.getLock("test");
        boolean locked = rLock.tryLock();
        if(locked) {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else{
            return "lock fail";
        }
        rLock.unlock();

        return "lock success";
    }


}
