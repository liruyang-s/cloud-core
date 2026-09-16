package com.micro.cloud.common.redis.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Redisson 分布式锁工具
 */
@Slf4j
@RequiredArgsConstructor
public class DistributedLockUtils {

    private final RedissonClient redissonClient;

    /**
     * 加锁执行业务
     *
     * @param lockKey   锁键
     * @param waitTime  等待获取锁时间（秒）
     * @param leaseTime 锁自动释放时间（秒）
     * @param supplier  业务逻辑
     */
    public <T> T executeWithLock(String lockKey, long waitTime, long leaseTime, Supplier<T> supplier) {
        RLock lock = redissonClient.getLock(lockKey);
        boolean locked = false;
        try {
            locked = lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
            if (!locked) {
                throw new com.micro.cloud.common.core.exception.ServiceException("操作过于频繁，请稍后再试");
            }
            return supplier.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new com.micro.cloud.common.core.exception.ServiceException("获取分布式锁被中断");
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
