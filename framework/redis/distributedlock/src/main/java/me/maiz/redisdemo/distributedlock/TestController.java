package me.maiz.redisdemo.distributedlock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * NOTE: 因为测试方式问题，发送两次锁定请求后，再解锁会产生死锁，因为第一次的clientId被覆盖了，实际使用中需要注意
 */
@RestController
public class TestController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping("testLock")
    public String get(String id, int expireTime,HttpSession session){
        String clientId = UUID.randomUUID().toString();
        session.setAttribute("clientId",clientId);
        System.out.println("锁定"+id+" with "+clientId+" TTL:"+ expireTime +"s");

        while(!DistributedLock.lock(redisTemplate.opsForValue(),id,clientId, expireTime)){

        }
        System.out.println("得到锁");

        return "success";

    }

    @GetMapping("testUnlock")
    public String set(String id,HttpSession session){
        String clientId = (String) session.getAttribute("clientId");
        System.out.println("解锁"+id+" with "+clientId);

        Boolean unlock = DistributedLock.unlock(redisTemplate, id, clientId);
        return unlock?"success":"fail";

    }
}
