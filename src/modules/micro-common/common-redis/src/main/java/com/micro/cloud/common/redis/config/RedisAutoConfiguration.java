package com.micro.cloud.common.redis.config;

import com.micro.cloud.common.redis.utils.CacheUtils;
import com.micro.cloud.common.redis.utils.DistributedLockUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.spring.starter.RedissonAutoConfigurationV2;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

/**
 * common-redis 自动装配入口
 * <p>
 * 先于 Redisson 官方自动装配注册自定义 RedissonClient（官方 @ConditionalOnMissingBean 会自动跳过）
 */
@Configuration
@Import(RedisConfig.class)
@AutoConfigureBefore(RedissonAutoConfigurationV2.class)
public class RedisAutoConfiguration {

    @Bean
    public CacheUtils cacheUtils(RedisTemplate<String, Object> redisTemplate) {
        return new CacheUtils(redisTemplate);
    }

    /**
     * 自定义 RedissonClient：空密码时不发送 AUTH
     * （Redisson 默认装配会把空字符串密码当作有效密码，导致无密码 Redis 报 "Client sent AUTH"）
     */
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient(RedisProperties redisProperties) {
        Config config = new Config();
        String password = StringUtils.hasText(redisProperties.getPassword())
                ? redisProperties.getPassword() : null;
        config.useSingleServer()
                .setAddress("redis://" + redisProperties.getHost() + ":" + redisProperties.getPort())
                .setDatabase(redisProperties.getDatabase())
                .setPassword(password);
        return Redisson.create(config);
    }

    @Bean
    public DistributedLockUtils distributedLockUtils(RedissonClient redissonClient) {
        return new DistributedLockUtils(redissonClient);
    }
}
