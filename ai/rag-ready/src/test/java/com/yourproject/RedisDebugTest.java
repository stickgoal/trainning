package com.yourproject;

import org.junit.jupiter.api.Test;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.commands.ProtocolCommand;
import redis.clients.jedis.util.SafeEncoder;

import java.util.List;

/**
 * 调试 Redis Stack HNSW 索引创建
 */
public class RedisDebugTest {

    @Test
    public void testRedisConnection() {
        JedisPooled jedis = new JedisPooled("192.168.1.241", 6379, null, "abc123");

        // HNSW 带参数个数 = 6 (TYPE, DIM, DISTANCE_METRIC, EFCONSTRUCTION, M)
        // 实际是 5 对 = 10 个参数
        tryDrop(jedis, "test_hnsw1");
        try {
            Object result = jedis.sendCommand(cmd("FT.CREATE"),
                    "test_hnsw1",
                    "ON", "JSON",
                    "PREFIX", "1", "testh1:",
                    "SCHEMA",
                    "$.text", "AS", "text", "TEXT",
                    "$.embedding", "AS", "embedding",
                    "VECTOR", "HNSW", "10",
                    "TYPE", "FLOAT32",
                    "DIM", "4",
                    "DISTANCE_METRIC", "COSINE",
                    "EFCONSTRUCTION", "200",
                    "M", "16"
            );
            System.out.println("HNSW with 10 params: SUCCESS");
        } catch (Exception e) {
            System.out.println("HNSW with 10 params FAILED: " + e.getMessage());
        }

        // HNSW 只必须参数 3 对 = 6 个参数
        tryDrop(jedis, "test_hnsw2");
        try {
            Object result = jedis.sendCommand(cmd("FT.CREATE"),
                    "test_hnsw2",
                    "ON", "JSON",
                    "PREFIX", "1", "testh2:",
                    "SCHEMA",
                    "$.text", "AS", "text", "TEXT",
                    "$.embedding", "AS", "embedding",
                    "VECTOR", "HNSW", "6",
                    "TYPE", "FLOAT32",
                    "DIM", "4",
                    "DISTANCE_METRIC", "COSINE"
            );
            System.out.println("HNSW with 6 params: SUCCESS");
        } catch (Exception e) {
            System.out.println("HNSW with 6 params FAILED: " + e.getMessage());
        }

        // HNSW 4 对 = 8 个参数 (加 EFCONSTRUCTION)
        tryDrop(jedis, "test_hnsw3");
        try {
            Object result = jedis.sendCommand(cmd("FT.CREATE"),
                    "test_hnsw3",
                    "ON", "JSON",
                    "PREFIX", "1", "testh3:",
                    "SCHEMA",
                    "$.text", "AS", "text", "TEXT",
                    "$.embedding", "AS", "embedding",
                    "VECTOR", "HNSW", "8",
                    "TYPE", "FLOAT32",
                    "DIM", "4",
                    "DISTANCE_METRIC", "COSINE",
                    "EFCONSTRUCTION", "200"
            );
            System.out.println("HNSW with 8 params: SUCCESS");
        } catch (Exception e) {
            System.out.println("HNSW with 8 params FAILED: " + e.getMessage());
        }

        // 清理
        tryDrop(jedis, "test_hnsw1");
        tryDrop(jedis, "test_hnsw2");
        tryDrop(jedis, "test_hnsw3");

        jedis.close();
    }

    private void tryDrop(JedisPooled jedis, String idx) {
        try { jedis.sendCommand(cmd("FT.DROPINDEX"), idx); } catch (Exception ignored) {}
    }

    private ProtocolCommand cmd(String command) {
        return () -> SafeEncoder.encode(command);
    }
}
