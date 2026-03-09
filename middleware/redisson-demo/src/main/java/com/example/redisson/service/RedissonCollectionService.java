package com.example.redisson.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.redisson.client.protocol.ScoredEntry;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedissonCollectionService {

    private final RedissonClient redissonClient;

    // RMap operations
    public String putToMap(String mapKey, String key, String value) {
        RMap<String, String> map = redissonClient.getMap(mapKey);
        String previousValue = map.put(key, value);
        log.info("Put to map: {} key: {} value: {} previous: {}", mapKey, key, value, previousValue);
        return previousValue;
    }

    public String getFromMap(String mapKey, String key) {
        RMap<String, String> map = redissonClient.getMap(mapKey);
        String value = map.get(key);
        log.info("Get from map: {} key: {} value: {}", mapKey, key, value);
        return value;
    }

    public Map<String, String> getAllFromMap(String mapKey) {
        RMap<String, String> map = redissonClient.getMap(mapKey);
        Map<String, String> allValues = map.readAllMap();
        log.info("Get all from map: {} size: {}", mapKey, allValues.size());
        return allValues;
    }

    public String removeFromMap(String mapKey, String key) {
        RMap<String, String> map = redissonClient.getMap(mapKey);
        String removedValue = map.remove(key);
        log.info("Remove from map: {} key: {} removed: {}", mapKey, key, removedValue);
        return removedValue;
    }

    public int getMapSize(String mapKey) {
        RMap<String, String> map = redissonClient.getMap(mapKey);
        int size = map.size();
        log.info("Map size: {} size: {}", mapKey, size);
        return size;
    }

    // RMap with TTL
    public String putToMapWithTTL(String mapKey, String key, String value, long ttl, TimeUnit timeUnit) {
        RMap<String, String> map = redissonClient.getMap(mapKey);
        map.expire(ttl, timeUnit);
        String previousValue = map.put(key, value);
        log.info("Put to map with TTL: {} key: {} value: {} ttl: {} {}", mapKey, key, value, ttl, timeUnit);
        return previousValue;
    }

    // RSet operations
    public boolean addToSet(String setKey, String value) {
        RSet<String> set = redissonClient.getSet(setKey);
        boolean added = set.add(value);
        log.info("Add to set: {} value: {} added: {}", setKey, value, added);
        return added;
    }

    public boolean removeFromSet(String setKey, String value) {
        RSet<String> set = redissonClient.getSet(setKey);
        boolean removed = set.remove(value);
        log.info("Remove from set: {} value: {} removed: {}", setKey, value, removed);
        return removed;
    }

    public Set<String> getAllFromSet(String setKey) {
        RSet<String> set = redissonClient.getSet(setKey);
        Set<String> allValues = set.readAll();
        log.info("Get all from set: {} size: {}", setKey, allValues.size());
        return allValues;
    }

    public int getSetSize(String setKey) {
        RSet<String> set = redissonClient.getSet(setKey);
        int size = set.size();
        log.info("Set size: {} size: {}", setKey, size);
        return size;
    }

    // Set operations (union, intersection, difference)
    public Set<String> setUnion(String setKey1, String setKey2, String resultKey) {
        RSet<String> set1 = redissonClient.getSet(setKey1);
        RSet<String> set2 = redissonClient.getSet(setKey2);
        RSet<String> resultSet = redissonClient.getSet(resultKey);
        
        resultSet.addAll(set1.readAll());
        resultSet.addAll(set2.readAll());
        
        Set<String> result = resultSet.readAll();
        log.info("Set union: {} ∪ {} = {} size: {}", setKey1, setKey2, resultKey, result.size());
        return result;
    }

    public Set<String> setIntersection(String setKey1, String setKey2, String resultKey) {
        RSet<String> set1 = redissonClient.getSet(setKey1);
        RSet<String> set2 = redissonClient.getSet(setKey2);
        RSet<String> resultSet = redissonClient.getSet(resultKey);
        
        Set<String> values1 = set1.readAll();
        Set<String> values2 = set2.readAll();
        
        values1.retainAll(values2);
        resultSet.addAll(values1);
        
        Set<String> result = resultSet.readAll();
        log.info("Set intersection: {} ∩ {} = {} size: {}", setKey1, setKey2, resultKey, result.size());
        return result;
    }

    // RList operations
    public boolean addToList(String listKey, String value) {
        RList<String> list = redissonClient.getList(listKey);
        boolean added = list.add(value);
        log.info("Add to list: {} value: {} added: {}", listKey, value, added);
        return added;
    }

    public String getFromList(String listKey, int index) {
        RList<String> list = redissonClient.getList(listKey);
        String value = list.get(index);
        log.info("Get from list: {} index: {} value: {}", listKey, index, value);
        return value;
    }

    public List<String> getAllFromList(String listKey) {
        RList<String> list = redissonClient.getList(listKey);
        List<String> allValues = list.readAll();
        log.info("Get all from list: {} size: {}", listKey, allValues.size());
        return allValues;
    }

    public String removeFromList(String listKey, int index) {
        RList<String> list = redissonClient.getList(listKey);
        String removedValue = list.remove(index);
        log.info("Remove from list: {} index: {} removed: {}", listKey, index, removedValue);
        return removedValue;
    }

    public int getListSize(String listKey) {
        RList<String> list = redissonClient.getList(listKey);
        int size = list.size();
        log.info("List size: {} size: {}", listKey, size);
        return size;
    }

    // RQueue operations
    public boolean offerToQueue(String queueKey, String value) {
        RQueue<String> queue = redissonClient.getQueue(queueKey);
        boolean offered = queue.offer(value);
        log.info("Offer to queue: {} value: {} offered: {}", queueKey, value, offered);
        return offered;
    }

    public String pollFromQueue(String queueKey) {
        RQueue<String> queue = redissonClient.getQueue(queueKey);
        String value = queue.poll();
        log.info("Poll from queue: {} value: {}", queueKey, value);
        return value;
    }

    public String peekFromQueue(String queueKey) {
        RQueue<String> queue = redissonClient.getQueue(queueKey);
        String value = queue.peek();
        log.info("Peek from queue: {} value: {}", queueKey, value);
        return value;
    }

    public int getQueueSize(String queueKey) {
        RQueue<String> queue = redissonClient.getQueue(queueKey);
        int size = queue.size();
        log.info("Queue size: {} size: {}", queueKey, size);
        return size;
    }

    // RScoredSortedSet operations
    public boolean addToSortedSet(String sortedSetKey, String member, double score) {
        RScoredSortedSet<String> sortedSet = redissonClient.getScoredSortedSet(sortedSetKey);
        boolean added = sortedSet.add(score, member);
        log.info("Add to sorted set: {} member: {} score: {} added: {}", sortedSetKey, member, score, added);
        return added;
    }

    public Collection<ScoredEntry<String>> getRangeFromSortedSet(String sortedSetKey, int startIndex, int endIndex) {
        RScoredSortedSet<String> sortedSet = redissonClient.getScoredSortedSet(sortedSetKey);
        Collection<ScoredEntry<String>> range = sortedSet.entryRange(startIndex, endIndex);
        log.info("Get range from sorted set: {} start: {} end: {} size: {}", sortedSetKey, startIndex, endIndex, range.size());
        return range;
    }

    public Double getScoreFromSortedSet(String sortedSetKey, String member) {
        RScoredSortedSet<String> sortedSet = redissonClient.getScoredSortedSet(sortedSetKey);
        Double score = sortedSet.getScore(member);
        log.info("Get score from sorted set: {} member: {} score: {}", sortedSetKey, member, score);
        return score;
    }

    public boolean removeFromSortedSet(String sortedSetKey, String member) {
        RScoredSortedSet<String> sortedSet = redissonClient.getScoredSortedSet(sortedSetKey);
        boolean removed = sortedSet.remove(member);
        log.info("Remove from sorted set: {} member: {} removed: {}", sortedSetKey, member, removed);
        return removed;
    }

    public int getSortedSetSize(String sortedSetKey) {
        RScoredSortedSet<String> sortedSet = redissonClient.getScoredSortedSet(sortedSetKey);
        int size = sortedSet.size();
        log.info("Sorted set size: {} size: {}", sortedSetKey, size);
        return size;
    }

    // RDeque operations
    public void addFirstToDeque(String dequeKey, String value) {
        RDeque<String> deque = redissonClient.getDeque(dequeKey);
        deque.addFirst(value);
        log.info("Add first to deque: {} value: {}", dequeKey, value);
    }

    public void addLastToDeque(String dequeKey, String value) {
        RDeque<String> deque = redissonClient.getDeque(dequeKey);
        deque.addLast(value);
        log.info("Add last to deque: {} value: {}", dequeKey, value);
    }

    public String getFirstFromDeque(String dequeKey) {
        RDeque<String> deque = redissonClient.getDeque(dequeKey);
        String value = deque.getFirst();
        log.info("Get first from deque: {} value: {}", dequeKey, value);
        return value;
    }

    public String getLastFromDeque(String dequeKey) {
        RDeque<String> deque = redissonClient.getDeque(dequeKey);
        String value = deque.getLast();
        log.info("Get last from deque: {} value: {}", dequeKey, value);
        return value;
    }

    public String removeFirstFromDeque(String dequeKey) {
        RDeque<String> deque = redissonClient.getDeque(dequeKey);
        String value = deque.removeFirst();
        log.info("Remove first from deque: {} value: {}", dequeKey, value);
        return value;
    }

    public String removeLastFromDeque(String dequeKey) {
        RDeque<String> deque = redissonClient.getDeque(dequeKey);
        String value = deque.removeLast();
        log.info("Remove last from deque: {} value: {}", dequeKey, value);
        return value;
    }

    // Cleanup operations
    public String deleteCollection(String collectionKey) {
        boolean deleted = redissonClient.getKeys().delete(collectionKey) > 0;
        log.info("Delete collection: {} success: {}", collectionKey, deleted);
        return deleted ? "OK" : "Collection not found";
    }
}
