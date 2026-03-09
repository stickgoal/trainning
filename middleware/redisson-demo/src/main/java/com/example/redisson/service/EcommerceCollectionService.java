package com.example.redisson.service;

import com.example.redisson.model.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EcommerceCollectionService {

    private final RedissonClient redissonClient;

    /**
     * 分布式Map案例 - 购物车管理
     * 场景：用户购物车商品的增删改查，支持过期时间
     */
    public String addToCart(String userId, String productId, Integer quantity) {
        String cartKey = "cart:" + userId;
        RMap<String, Integer> cart = redissonClient.getMap(cartKey);
        
        // 设置购物车1小时后过期
        cart.expire(1, TimeUnit.HOURS);
        
        Integer currentQuantity = cart.getOrDefault(productId, 0);
        cart.put(productId, currentQuantity + quantity);
        
        log.info("用户 {} 添加商品 {} 到购物车，数量: {}", userId, productId, quantity);
        return String.format("商品 %s 已添加到购物车，当前数量: %d", productId, currentQuantity + quantity);
    }
    
    public String updateCartQuantity(String userId, String productId, Integer quantity) {
        String cartKey = "cart:" + userId;
        RMap<String, Integer> cart = redissonClient.getMap(cartKey);
        
        if (quantity <= 0) {
            cart.remove(productId);
            log.info("用户 {} 从购物车移除商品 {}", userId, productId);
            return String.format("商品 %s 已从购物车移除", productId);
        } else {
            cart.put(productId, quantity);
            log.info("用户 {} 更新购物车商品 {} 数量为 {}", userId, productId, quantity);
            return String.format("商品 %s 数量已更新为 %d", productId, quantity);
        }
    }
    
    public Map<String, Integer> getCart(String userId) {
        String cartKey = "cart:" + userId;
        RMap<String, Integer> cart = redissonClient.getMap(cartKey);
        
        Map<String, Integer> cartItems = cart.readAllMap();
        log.info("用户 {} 购物车商品数量: {}", userId, cartItems.size());
        return cartItems;
    }
    
    public String clearCart(String userId) {
        String cartKey = "cart:" + userId;
        RMap<String, Integer> cart = redissonClient.getMap(cartKey);
        
        cart.clear();
        log.info("用户 {} 购物车已清空", userId);
        return "购物车已清空";
    }
    
    /**
     * 分布式Set案例 - 商品收藏夹
     * 场景：用户收藏商品，支持交集、并集操作
     */
    public String addToFavorites(String userId, String productId) {
        String favoritesKey = "favorites:" + userId;
        RSet<String> favorites = redissonClient.getSet(favoritesKey);
        
        boolean added = favorites.add(productId);
        if (added) {
            log.info("用户 {} 收藏商品 {}", userId, productId);
            return String.format("商品 %s 已添加到收藏夹", productId);
        } else {
            return String.format("商品 %s 已在收藏夹中", productId);
        }
    }
    
    public Set<String> getFavorites(String userId) {
        String favoritesKey = "favorites:" + userId;
        RSet<String> favorites = redissonClient.getSet(favoritesKey);
        
        Set<String> favoriteItems = favorites.readAll();
        log.info("用户 {} 收藏商品数量: {}", userId, favoriteItems.size());
        return favoriteItems;
    }
    
    public String removeFromFavorites(String userId, String productId) {
        String favoritesKey = "favorites:" + userId;
        RSet<String> favorites = redissonClient.getSet(favoritesKey);
        
        boolean removed = favorites.remove(productId);
        if (removed) {
            log.info("用户 {} 取消收藏商品 {}", userId, productId);
            return String.format("商品 %s 已从收藏夹移除", productId);
        } else {
            return String.format("商品 %s 不在收藏夹中", productId);
        }
    }
    
    /**
     * 收藏夹交集 - 找出多个用户共同收藏的商品
     */
    public Set<String> getCommonFavorites(String... userIds) {
        if (userIds.length < 2) {
            return Collections.emptySet();
        }
        
        String[] favoritesKeys = new String[userIds.length];
        for (int i = 0; i < userIds.length; i++) {
            favoritesKeys[i] = "favorites:" + userIds[i];
        }
        
        RSet<String> intersection = redissonClient.getSet("temp:intersection");
        intersection.clear();
        
        // 获取第一个用户的收藏夹作为基础
        RSet<String> firstFavorites = redissonClient.getSet(favoritesKeys[0]);
        intersection.addAll(firstFavorites.readAll());
        
        // 与其他用户的收藏夹求交集
        for (int i = 1; i < favoritesKeys.length; i++) {
            RSet<String> currentFavorites = redissonClient.getSet(favoritesKeys[i]);
            intersection.retainAll(currentFavorites.readAll());
        }
        
        Set<String> result = intersection.readAll();
        log.info("用户 {} 共同收藏商品数量: {}", Arrays.toString(userIds), result.size());
        
        // 清理临时集合
        intersection.delete();
        
        return result;
    }
    
    /**
     * 分布式List案例 - 商品浏览历史
     * 场景：记录用户浏览商品的历史，支持分页查询
     */
    public String addToBrowseHistory(String userId, String productId) {
        String historyKey = "browse_history:" + userId;
        RList<String> history = redissonClient.getList(historyKey);
        
        // 限制历史记录最多保存100条
        if (history.size() >= 100) {
            history.trim(0, 98); // 移除最旧的记录
        }
        
        history.add(productId + ":" + LocalDateTime.now());
        log.info("用户 {} 浏览商品 {}", userId, productId);
        return String.format("商品 %s 已添加到浏览历史", productId);
    }
    
    public List<String> getBrowseHistory(String userId, int page, int size) {
        String historyKey = "browse_history:" + userId;
        RList<String> history = redissonClient.getList(historyKey);
        
        int start = (page - 1) * size;
        int end = Math.min(start + size, history.size());
        
        List<String> historyItems = history.subList(start, end);
        log.info("用户 {} 浏览历史第 {} 页，记录数: {}", userId, page, historyItems.size());
        return historyItems;
    }
    
    /**
     * 分布式Queue案例 - 订单处理队列
     * 场景：异步处理订单，支持优先级队列
     */
    public String submitOrder(String orderId, String userId, Double amount) {
        String orderQueueKey = "order:queue";
        RQueue<String> orderQueue = redissonClient.getQueue(orderQueueKey);
        
        String orderInfo = String.format("%s:%s:%.2f:%s", orderId, userId, amount, LocalDateTime.now());
        orderQueue.add(orderInfo);
        
        log.info("订单 {} 已提交到处理队列", orderId);
        return String.format("订单 %s 已提交，等待处理", orderId);
    }
    
    public String processNextOrder() {
        String orderQueueKey = "order:queue";
        RQueue<String> orderQueue = redissonClient.getQueue(orderQueueKey);
        
        String orderInfo = orderQueue.poll();
        if (orderInfo != null) {
            String[] parts = orderInfo.split(":");
            String orderId = parts[0];
            String userId = parts[1];
            Double amount = Double.parseDouble(parts[2]);
            
            log.info("处理订单: {} - 用户: {}, 金额: ¥{}", orderId, userId, amount);
            
            // 模拟订单处理
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            return String.format("订单 %s 处理完成", orderId);
        } else {
            return "暂无待处理订单";
        }
    }
    
    public int getPendingOrderCount() {
        String orderQueueKey = "order:queue";
        RQueue<String> orderQueue = redissonClient.getQueue(orderQueueKey);
        return orderQueue.size();
    }
    
    /**
     * 分布式优先级队列案例 - VIP订单优先处理
     */
    public String submitPriorityOrder(String orderId, String userId, Double amount, String vipLevel) {
        String priorityQueueKey = "order:priority_queue";
        RPriorityQueue<String> priorityQueue = redissonClient.getPriorityQueue(priorityQueueKey);
        
        // VIP等级越高，优先级越高（数值越小优先级越高）
        int priority = getVipPriority(vipLevel);
        String orderInfo = String.format("%d:%s:%s:%.2f:%s", priority, orderId, userId, amount, LocalDateTime.now());
        
        priorityQueue.trySetComparator(Comparator.comparingInt(String -> Integer.parseInt(String.split(":")[0])));
        priorityQueue.add(orderInfo);
        
        log.info("VIP订单 {} 已提交，优先级: {}", orderId, priority);
        return String.format("VIP订单 %s 已提交，优先级: %d", orderId, priority);
    }
    
    public String processNextPriorityOrder() {
        String priorityQueueKey = "order:priority_queue";
        RPriorityQueue<String> priorityQueue = redissonClient.getPriorityQueue(priorityQueueKey);
        
        String orderInfo = priorityQueue.poll();
        if (orderInfo != null) {
            String[] parts = orderInfo.split(":");
            int priority = Integer.parseInt(parts[0]);
            String orderId = parts[1];
            String userId = parts[2];
            Double amount = Double.parseDouble(parts[3]);
            
            log.info("处理优先级订单: {} (优先级: {}) - 用户: {}, 金额: ¥{}", orderId, priority, userId, amount);
            
            // 模拟订单处理
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            return String.format("优先级订单 %s 处理完成", orderId);
        } else {
            return "暂无待处理优先级订单";
        }
    }
    
    /**
     * 分布式有序集合案例 - 商品排行榜
     * 场景：商品销量排行榜，支持实时更新和范围查询
     */
    public String recordProductSale(String productId, Integer quantity) {
        String salesRankingKey = "product:sales_ranking";
        RScoredSortedSet<String> salesRanking = redissonClient.getScoredSortedSet(salesRankingKey);
        
        // 增加销量分数
        salesRanking.addScore(productId, quantity);
        
        Double currentScore = salesRanking.getScore(productId);
        log.info("商品 {} 销量增加 {}, 总销量: {}", productId, quantity, currentScore.intValue());
        return String.format("商品 %s 销量记录成功，总销量: %d", productId, currentScore.intValue());
    }
    
    public List<Map<String, Object>> getTopProducts(int topN) {
        String salesRankingKey = "product:sales_ranking";
        RScoredSortedSet<String> salesRanking = redissonClient.getScoredSortedSet(salesRankingKey);
        
        // 获取销量最高的topN个商品（倒序）
        Collection<ScoredEntry<String>> topProducts = salesRanking.entryRange(salesRanking.size() - topN, -1);
        
        List<Map<String, Object>> result = new ArrayList<>();
        int rank = 1;
        
        // 倒序排列
        List<ScoredEntry<String>> sortedList = new ArrayList<>(topProducts);
        sortedList.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
        
        for (ScoredEntry<String> entry : sortedList) {
            Map<String, Object> productInfo = new HashMap<>();
            productInfo.put("rank", rank++);
            productInfo.put("productId", entry.getValue());
            productInfo.put("sales", entry.getScore().intValue());
            result.add(productInfo);
        }
        
        log.info("获取商品销量排行榜 TOP {}", topN);
        return result;
    }
    
    public Integer getProductRank(String productId) {
        String salesRankingKey = "product:sales_ranking";
        RScoredSortedSet<String> salesRanking = redissonClient.getScoredSortedSet(salesRankingKey);
        
        Integer rank = salesRanking.revRank(productId);
        if (rank != null) {
            // Redisson的rank从0开始，转换为从1开始
            return rank + 1;
        }
        return null;
    }
    
    /**
     * 分布式双端队列案例 - 商品推荐缓存
     * 场景：LRU缓存策略，最近推荐的商品放在队首
     */
    public String addToRecommendationCache(String productId) {
        String cacheKey = "product:recommendation_cache";
        RDeque<String> cache = redissonClient.getDeque(cacheKey);
        
        // 如果商品已在缓存中，先移除
        cache.remove(productId);
        
        // 添加到队首
        cache.addFirst(productId);
        
        // 限制缓存大小
        while (cache.size() > 50) {
            cache.removeLast();
        }
        
        log.info("商品 {} 已添加到推荐缓存", productId);
        return String.format("商品 %s 已添加到推荐缓存队首", productId);
    }
    
    public List<String> getRecommendations(int count) {
        String cacheKey = "product:recommendation_cache";
        RDeque<String> cache = redissonClient.getDeque(cacheKey);
        
        List<String> recommendations = new ArrayList<>();
        for (int i = 0; i < Math.min(count, cache.size()); i++) {
            recommendations.add(cache.get(i));
        }
        
        log.info("获取推荐商品列表，数量: {}", recommendations.size());
        return recommendations;
    }
    
    /**
     * 分布式地理空间案例 - 附近店铺查询
     * 场景：基于地理位置的店铺推荐
     */
    public String addStoreLocation(String storeId, Double longitude, Double latitude) {
        String geoKey = "stores:locations";
        RGeo<String> geo = redissonClient.getGeo(geoKey);
        
        geo.add(longitude, latitude, storeId);
        log.info("店铺 {} 位置已添加: 经度={}, 纬度={}", storeId, longitude, latitude);
        return String.format("店铺 %s 位置添加成功", storeId);
    }
    
    public List<Map<String, Object>> findNearbyStores(Double longitude, Double latitude, Double radiusKm) {
        String geoKey = "stores:locations";
        RGeo<String> geo = redissonClient.getGeo(geoKey);
        
        List<Map<String, Object>> nearbyStores = new ArrayList<>();
        
        // 查找指定半径内的店铺
        geo.radius(longitude, latitude, radiusKm, GeoUnit.KILOMETERS)
           .forEach(entry -> {
               Map<String, Object> storeInfo = new HashMap<>();
               storeInfo.put("storeId", entry.getMember());
               storeInfo.put("distance", entry.getDistance());
               nearbyStores.add(storeInfo);
           });
        
        // 按距离排序
        nearbyStores.sort((a, b) -> Double.compare((Double) a.get("distance"), (Double) b.get("distance")));
        
        log.info("查找附近店铺，中心位置: ({}, {}), 半径: {}km, 找到 {} 家店铺", 
                longitude, latitude, radiusKm, nearbyStores.size());
        return nearbyStores;
    }
    
    private int getVipPriority(String vipLevel) {
        switch (vipLevel.toUpperCase()) {
            case "DIAMOND": return 1;
            case "GOLD": return 2;
            case "SILVER": return 3;
            case "BRONZE": return 4;
            default: return 5;
        }
    }
}
