package com.micro.cloud.common.rocketmq.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * 消息幂等消费模板
 * <p>
 * 基于 Redis SETNX 实现消费幂等：同一 msgId 在过期时间内仅消费一次，
 * 防止 RocketMQ 重试机制导致的重复消费。
 */
@Slf4j
@RequiredArgsConstructor
public class IdempotentConsumerTemplate {

    private static final String KEY_PREFIX = "mq:idempotent:";

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 幂等执行业务
     *
     * @param msgId    消息唯一ID
     * @param expire   幂等键过期时间
     * @param consumer 业务消费逻辑
     * @return 是否实际执行（false 表示重复消息已跳过）
     */
    public boolean executeOnce(String msgId, Duration expire, Supplier<Boolean> consumer) {
        Boolean firstTime = stringRedisTemplate.opsForValue()
                .setIfAbsent(KEY_PREFIX + msgId, "1", expire);
        if (!Boolean.TRUE.equals(firstTime)) {
            log.warn("重复消息已跳过, msgId={}", msgId);
            return false;
        }
        try {
            return Boolean.TRUE.equals(consumer.get());
        } catch (Exception e) {
            // 消费失败释放幂等键，允许重试
            stringRedisTemplate.delete(KEY_PREFIX + msgId);
            throw e;
        }
    }
}
