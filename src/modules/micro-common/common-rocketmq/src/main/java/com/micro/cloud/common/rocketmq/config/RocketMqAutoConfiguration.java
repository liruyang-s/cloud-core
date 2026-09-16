package com.micro.cloud.common.rocketmq.config;

import com.micro.cloud.common.rocketmq.core.IdempotentConsumerTemplate;
import com.micro.cloud.common.rocketmq.utils.MessageProducer;
import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * common-rocketmq 自动装配入口
 * <p>
 * 必须在 RocketMQ 官方自动装配之后处理，否则 @ConditionalOnBean(RocketMQTemplate) 因装配顺序提前而失效
 */
@Configuration
@AutoConfigureAfter(RocketMQAutoConfiguration.class)
public class RocketMqAutoConfiguration {

    @Bean
    @ConditionalOnBean(RocketMQTemplate.class)
    public MessageProducer messageProducer(RocketMQTemplate rocketMQTemplate) {
        return new MessageProducer(rocketMQTemplate);
    }

    @Bean
    public IdempotentConsumerTemplate idempotentConsumerTemplate(StringRedisTemplate stringRedisTemplate) {
        return new IdempotentConsumerTemplate(stringRedisTemplate);
    }
}
