package com.example.redisson.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EcommerceBasicService {

    private final RedissonClient redissonClient;

    /**
     * 用户会话管理 - String操作
     * 场景：用户登录状态的存储和管理
     */
    public String setUserSession(String userId, String sessionId, long ttlHours) {
        RBucket<String> sessionBucket = redissonClient.getBucket("session:" + userId);
        sessionBucket.set(sessionId, ttlHours, TimeUnit.HOURS);
        
        log.info("设置用户会话: 用户={}, 会话ID={}, TTL={}小时", userId, sessionId, ttlHours);
        return String.format("用户 %s 会话设置成功，有效期 %d 小时", userId, ttlHours);
    }
    
    public String getUserSession(String userId) {
        RBucket<String> sessionBucket = redissonClient.getBucket("session:" + userId);
        String sessionId = sessionBucket.get();
        
        if (sessionId != null) {
            log.info("获取用户会话: 用户={}, 会话ID={}", userId, sessionId);
            return sessionId;
        } else {
            log.warn("用户会话不存在: 用户={}", userId);
            return "会话不存在";
        }
    }
    
    public boolean isUserOnline(String userId) {
        RBucket<String> sessionBucket = redissonClient.getBucket("session:" + userId);
        boolean isOnline = sessionBucket.isExists();
        log.info("检查用户在线状态: 用户={}, 在线={}", userId, isOnline);
        return isOnline;
    }
    
    /**
     * 商品信息缓存 - Hash操作
     * 场景：商品详细信息的结构化存储
     */
    public String cacheProductInfo(String productId, String name, Double price, String category, Integer stock) {
        RMap<String, String> productMap = redissonClient.getMap("product:" + productId);
        
        Map<String, String> productData = new HashMap<>();
        productData.put("name", name);
        productData.put("price", price.toString());
        productData.put("category", category);
        productData.put("stock", stock.toString());
        productData.put("cachedAt", LocalDateTime.now().toString());
        
        productMap.putAll(productData);
        productMap.expire(1, TimeUnit.HOURS); // 1小时过期
        
        log.info("缓存商品信息: 商品={}, 名称={}, 价格=¥{}", productId, name, price);
        return String.format("商品 %s 信息缓存成功", productId);
    }
    
    public Map<String, String> getProductInfo(String productId) {
        RMap<String, String> productMap = redissonClient.getMap("product:" + productId);
        Map<String, String> productInfo = productMap.readAllMap();
        
        if (!productInfo.isEmpty()) {
            log.info("获取商品信息: 商品={}, 字段数={}", productId, productInfo.size());
        } else {
            log.warn("商品信息不存在: 商品={}", productId);
        }
        
        return productInfo;
    }
    
    public String updateProductField(String productId, String field, String value) {
        RMap<String, String> productMap = redissonClient.getMap("product:" + productId);
        String oldValue = productMap.put(field, value);
        
        log.info("更新商品字段: 商品={}, 字段={}, 旧值={}, 新值={}", productId, field, oldValue, value);
        return String.format("商品 %s 字段 %s 更新成功", productId, field);
    }
    
    /**
     * 用户浏览历史 - List操作
     * 场景：记录用户的商品浏览历史
     */
    public String addToBrowseHistory(String userId, String productId) {
        RList<String> historyList = redissonClient.getList("browse_history:" + userId);
        
        String historyItem = productId + ":" + LocalDateTime.now();
        historyList.add(historyItem);
        
        // 限制历史记录数量
        if (historyList.size() > 100) {
            historyList.trim(0, 98); // 保留最新的99条
        }
        
        log.info("添加浏览历史: 用户={}, 商品={}", userId, productId);
        return String.format("商品 %s 已添加到用户 %s 的浏览历史", productId, userId);
    }
    
    public List<String> getBrowseHistory(String userId, int limit) {
        RList<String> historyList = redissonClient.getList("browse_history:" + userId);
        
        int size = historyList.size();
        int start = Math.max(0, size - limit);
        
        List<String> recentHistory = historyList.subList(start, size);
        log.info("获取浏览历史: 用户={}, 数量={}", userId, recentHistory.size());
        return recentHistory;
    }
    
    public String clearBrowseHistory(String userId) {
        RList<String> historyList = redissonClient.getList("browse_history:" + userId);
        int count = historyList.size();
        historyList.clear();
        
        log.info("清空浏览历史: 用户={}, 清理记录数={}", userId, count);
        return String.format("用户 %s 的浏览历史已清空，清理了 %d 条记录", userId, count);
    }
    
    /**
     * 用户标签管理 - Set操作
     * 场景：用户兴趣标签的存储和管理
     */
    public String addUserTag(String userId, String tag) {
        RSet<String> tagSet = redissonClient.getSet("user_tags:" + userId);
        boolean added = tagSet.add(tag);
        
        if (added) {
            log.info("添加用户标签: 用户={}, 标签={}", userId, tag);
            return String.format("标签 %s 已添加到用户 %s", tag, userId);
        } else {
            return String.format("标签 %s 已存在于用户 %s", tag, userId);
        }
    }
    
    public Set<String> getUserTags(String userId) {
        RSet<String> tagSet = redissonClient.getSet("user_tags:" + userId);
        Set<String> tags = tagSet.readAll();
        log.info("获取用户标签: 用户={}, 标签数={}", userId, tags.size());
        return tags;
    }
    
    public String removeUserTag(String userId, String tag) {
        RSet<String> tagSet = redissonClient.getSet("user_tags:" + userId);
        boolean removed = tagSet.remove(tag);
        
        if (removed) {
            log.info("移除用户标签: 用户={}, 标签={}", userId, tag);
            return String.format("标签 %s 已从用户 %s 移除", tag, userId);
        } else {
            return String.format("标签 %s 不存在于用户 %s", tag, userId);
        }
    }
    
    /**
     * 商品评分排行 - Sorted Set操作
     * 场景：商品评分的排序存储
     */
    public String addProductRating(String productId, Double rating) {
        RScoredSortedSet<String> ratingSet = redissonClient.getScoredSortedSet("product_ratings");
        ratingSet.addScore(productId, rating);
        
        Double currentScore = ratingSet.getScore(productId);
        log.info("添加商品评分: 商品={}, 评分={}, 平均评分={}", productId, rating, currentScore);
        return String.format("商品 %s 评分添加成功，当前平均评分: %.2f", productId, currentScore);
    }
    
    public List<Map<String, Object>> getTopRatedProducts(int topN) {
        RScoredSortedSet<String> ratingSet = redissonClient.getScoredSortedSet("product_ratings");
        
        // 获取评分最高的商品
        Collection<ScoredEntry<String>> topProducts = ratingSet.entryRange(ratingSet.size() - topN, -1);
        
        List<Map<String, Object>> result = new ArrayList<>();
        int rank = 1;
        
        // 倒序排列
        List<ScoredEntry<String>> sortedList = new ArrayList<>(topProducts);
        sortedList.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
        
        for (ScoredEntry<String> entry : sortedList) {
            Map<String, Object> productInfo = new HashMap<>();
            productInfo.put("rank", rank++);
            productInfo.put("productId", entry.getValue());
            productInfo.put("rating", entry.getScore());
            result.add(productInfo);
        }
        
        log.info("获取高分商品排行榜 TOP {}", topN);
        return result;
    }
    
    /**
     * 消息队列 - Queue操作
     * 场景：异步消息处理
     */
    public String enqueueMessage(String queueName, String message) {
        RQueue<String> queue = redissonClient.getQueue(queueName);
        queue.add(message);
        
        log.info("入队消息: 队列={}, 消息={}", queueName, message);
        return String.format("消息已入队到 %s，当前队列长度: %d", queueName, queue.size());
    }
    
    public String dequeueMessage(String queueName) {
        RQueue<String> queue = redissonClient.getQueue(queueName);
        String message = queue.poll();
        
        if (message != null) {
            log.info("出队消息: 队列={}, 消息={}", queueName, message);
            return message;
        } else {
            log.warn("队列为空: 队列={}", queueName);
            return "队列为空";
        }
    }
    
    public int getQueueSize(String queueName) {
        RQueue<String> queue = redissonClient.getQueue(queueName);
        int size = queue.size();
        log.info("获取队列长度: 队列={}, 长度={}", queueName, size);
        return size;
    }
    
    /**
     * 分布式计数器 - 原子操作
     * 场景：商品点击量统计
     */
    public String incrementProductClick(String productId) {
        RAtomicLong clickCounter = redissonClient.getAtomicLong("clicks:" + productId);
        long clicks = clickCounter.incrementAndGet();
        
        log.info("商品点击计数: 商品={}, 点击数={}", productId, clicks);
        return String.format("商品 %s 点击数: %d", productId, clicks);
    }
    
    public long getProductClicks(String productId) {
        RAtomicLong clickCounter = redissonClient.getAtomicLong("clicks:" + productId);
        long clicks = clickCounter.get();
        log.info("获取商品点击数: 商品={}, 点击数={}", productId, clicks);
        return clicks;
    }
    
    public String resetProductClicks(String productId) {
        RAtomicLong clickCounter = redissonClient.getAtomicLong("clicks:" + productId);
        clickCounter.set(0);
        log.info("重置商品点击数: 商品={}", productId);
        return String.format("商品 %s 点击数已重置", productId);
    }
    
    /**
     * 分布式位图 - 用户签到
     * 场景：用户每日签到记录
     */
    public String userCheckIn(String userId, int dayOfYear) {
        RBitSet checkInBitSet = redissonClient.getBitSet("checkin:" + userId);
        checkInBitSet.set(dayOfYear);
        
        int checkedDays = checkInBitSet.cardinality();
        log.info("用户签到: 用户={}, 年内第{}天, 已签到{}天", userId, dayOfYear, checkedDays);
        return String.format("用户 %s 签到成功，年内已签到 %d 天", userId, checkedDays);
    }
    
    public boolean isUserCheckedIn(String userId, int dayOfYear) {
        RBitSet checkInBitSet = redissonClient.getBitSet("checkin:" + userId);
        boolean checked = checkInBitSet.get(dayOfYear);
        log.info("检查签到状态: 用户={}, 年内第{}天, 已签到={}", userId, dayOfYear, checked);
        return checked;
    }
    
    public int getUserCheckInDays(String userId) {
        RBitSet checkInBitSet = redissonClient.getBitSet("checkin:" + userId);
        int checkedDays = checkInBitSet.cardinality();
        log.info("获取用户签到天数: 用户={}, 已签到{}天", userId, checkedDays);
        return checkedDays;
    }
    
    /**
     * 分布式布隆过滤器 - 商品推荐去重
     * 场景：避免重复推荐相同商品
     */
    public String addToRecommendationFilter(String productId) {
        RBloomFilter<String> recommendationFilter = redissonClient.getBloomFilter("recommendation_filter");
        
        // 初始化布隆过滤器（如果需要）
        if (!recommendationFilter.isExists()) {
            recommendationFilter.tryInit(100000, 0.01); // 10万容量，1%误判率
        }
        
        boolean added = recommendationFilter.add(productId);
        log.info("添加到推荐过滤器: 商品={}, 可能新增={}", productId, added);
        return String.format("商品 %s 已添加到推荐过滤器", productId);
    }
    
    public boolean mightBeRecommended(String productId) {
        RBloomFilter<String> recommendationFilter = redissonClient.getBloomFilter("recommendation_filter");
        boolean mightContain = recommendationFilter.contains(productId);
        log.info("检查推荐过滤器: 商品={}, 可能存在={}", productId, mightContain);
        return mightContain;
    }
    
    /**
     * 分布式HyperLogLog - UV统计
     * 场景：网站独立访客统计
     */
    public String recordPageView(String pageId, String userId) {
        RHyperLogLog<String> pageViews = redissonClient.getHyperLogLog("page_views:" + pageId);
        pageViews.add(userId);
        
        long estimatedCount = pageViews.count();
        log.info("记录页面访问: 页面={}, 用户={}, 估算UV={}", pageId, userId, estimatedCount);
        return String.format("页面 %s 访问记录成功，估算UV: %d", pageId, estimatedCount);
    }
    
    public long getPageViewUV(String pageId) {
        RHyperLogLog<String> pageViews = redissonClient.getHyperLogLog("page_views:" + pageId);
        long estimatedCount = pageViews.count();
        log.info("获取页面UV: 页面={}, 估算UV={}", pageId, estimatedCount);
        return estimatedCount;
    }
    
    /**
     * 批量操作 - 商品数据批量导入
     * 场景：大量商品数据的批量处理
     */
    public String batchImportProducts(Map<String, Map<String, String>> products) {
        RBatch batch = redissonClient.createBatch();
        
        products.forEach((productId, productData) -> {
            RMapAsync<String, String> productMap = batch.getMap("product:" + productId);
            productMap.putAllAsync(productData);
            
            // 设置过期时间
            RBucketAsync<String> expireBucket = batch.getBucket("expire:" + productId);
            expireBucket.setAsync("1", 1, TimeUnit.HOURS);
        });
        
        BatchResult<?> result = batch.execute();
        int successCount = result.getResponses().size();
        
        log.info("批量导入商品: 导入数量={}, 成功数量={}", products.size(), successCount);
        return String.format("批量导入完成，成功导入 %d/%d 个商品", successCount, products.size());
    }
    
    /**
     * 键模式匹配 - 数据清理
     * 场景：清理过期的临时数据
     */
    public String cleanExpiredData(String pattern) {
        RKeys keys = redissonClient.getKeys();
        Iterable<String> expiredKeys = keys.getKeysByPattern(pattern);
        
        int deletedCount = 0;
        for (String key : expiredKeys) {
            redissonClient.getBucket(key).delete();
            deletedCount++;
        }
        
        log.info("清理过期数据: 模式={}, 删除数量={}", pattern, deletedCount);
        return String.format("清理完成，删除了 %d 个匹配 '%s' 的键", deletedCount, pattern);
    }
    
    /**
     * 管道操作 - 提升性能
     * 场景：多个命令的批量执行
     */
    public String pipelineUserActivity(String userId, List<String> activities) {
        RBatch batch = redissonClient.createBatch();
        
        // 批量记录用户活动
        for (String activity : activities) {
            RListAsync<String> activityList = batch.getList("user_activity:" + userId);
            activityList.addAsync(activity + ":" + LocalDateTime.now());
        }
        
        // 更新用户活跃度分数
        RAtomicLongAsync activityScore = batch.getAtomicLong("activity_score:" + userId);
        activityScore.addAndGetAsync(activities.size());
        
        BatchResult<?> result = batch.execute();
        log.info("管道记录用户活动: 用户={}, 活动数={}, 执行结果数={}", userId, activities.size(), result.getResponses().size());
        
        return String.format("用户 %s 的 %d 个活动已批量记录", userId, activities.size());
    }
}
