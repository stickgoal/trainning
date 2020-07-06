package me.maiz.redisdemo.distributedlock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.concurrent.TimeUnit;


public class DistributedLock {

    /**
     * 对指定的key加锁等待
     *
     * @param ops
     * @param lockKey
     * @param clientId
     * @param expireTime 防止死锁
     * @return
     */
    public static Boolean lock(ValueOperations ops, String lockKey, String clientId, int expireTime) {
        return ops.setIfAbsent(lockKey, clientId, expireTime, TimeUnit.SECONDS);
    }

    /**
     * 只有原始的客户端才可以解锁
     *
     * @param redisTemplate
     * @param lockKey
     * @param requestId
     * @return
     */
    public static Boolean unlock(RedisTemplate redisTemplate, String lockKey, String requestId) {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        redisScript.setScriptText(script);
        redisScript.setResultType(Long.class);
        Long result = (Long) redisTemplate.execute(redisScript,Collections.singletonList(lockKey),requestId);
        return result == 1L;


    }


}
