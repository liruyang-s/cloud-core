package com.micro.cloud.common.redis.utils;

import cn.hutool.core.util.RandomUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 两级缓存工具（Caffeine 一级 + Redis 二级）
 * <p>
 * 防穿透：空值缓存；防雪崩：随机 TTL。
 */
@RequiredArgsConstructor
public class CacheUtils {

    private final RedisTemplate<String, Object> redisTemplate;

    /** 空值占位符（防缓存穿透） */
    private static final Object NULL_PLACEHOLDER = new Object() {
    };

    /** 一级缓存：本地热点数据，最多 1000 条，5 分钟过期 */
    private static final Cache<String, Object> LOCAL_CACHE = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(Duration.ofMinutes(5))
            .build();

    /**
     * 读取缓存，未命中时通过 loader 加载并回写
     *
     * @param key     缓存键
     * @param ttl     基础过期时间（秒），实际过期时间 = ttl + 随机偏移（防雪崩）
     * @param loader  数据加载器
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, long ttl, Supplier<T> loader) {
        // 一级缓存
        Object local = LOCAL_CACHE.getIfPresent(key);
        if (local != null) {
            return local == NULL_PLACEHOLDER ? null : (T) local;
        }
        // 二级缓存
        Object remote = redisTemplate.opsForValue().get(key);
        if (remote != null) {
            LOCAL_CACHE.put(key, remote);
            return remote == NULL_PLACEHOLDER ? null : (T) remote;
        }
        // 回源加载
        T value = loader.get();
        if (value == null) {
            // 空值缓存，短过期，防穿透
            LOCAL_CACHE.put(key, NULL_PLACEHOLDER);
            redisTemplate.opsForValue().set(key, NULL_PLACEHOLDER, Duration.ofMinutes(2));
            return null;
        }
        long randomTtl = ttl + RandomUtil.randomLong(0, ttl / 10 + 60);
        LOCAL_CACHE.put(key, value);
        redisTemplate.opsForValue().set(key, value, randomTtl, TimeUnit.SECONDS);
        return value;
    }

    /** 写入缓存 */
    public void put(String key, Object value, long ttlSeconds) {
        LOCAL_CACHE.put(key, value);
        long randomTtl = ttlSeconds + RandomUtil.randomLong(0, ttlSeconds / 10 + 60);
        redisTemplate.opsForValue().set(key, value, randomTtl, TimeUnit.SECONDS);
    }

    /** 删除缓存（一级 + 二级） */
    public void remove(String key) {
        LOCAL_CACHE.invalidate(key);
        redisTemplate.delete(key);
    }
}
