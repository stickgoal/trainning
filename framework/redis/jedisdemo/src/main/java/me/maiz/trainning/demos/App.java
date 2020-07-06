package me.maiz.trainning.demos;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        JedisPool jedisPool = new JedisPool(buildPoolConfig(),"localhost");

        Jedis jedis = jedisPool.getResource();

        jedis.set("app.version","1");
        String s = jedis.get("app.version");
        System.out.println(s);
        jedis.incr("app.version");
        System.out.println(jedis.get("app.version"));

        jedis.sadd("groupOne","张三","李四","王五");

        System.out.println(jedis.srandmember("groupOne"));
        System.out.println(jedis.scard("groupOne"));

        jedis.close();

        jedisPool.close();
    }


    public static JedisPoolConfig buildPoolConfig() {
        final JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(128);
        poolConfig.setMaxIdle(128);
        poolConfig.setMinIdle(16);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setMinEvictableIdleTimeMillis(Duration.ofSeconds(60).toMillis());
        poolConfig.setTimeBetweenEvictionRunsMillis(Duration.ofSeconds(30).toMillis());
        poolConfig.setNumTestsPerEvictionRun(3);
        poolConfig.setBlockWhenExhausted(true);
        return poolConfig;
    }
}
