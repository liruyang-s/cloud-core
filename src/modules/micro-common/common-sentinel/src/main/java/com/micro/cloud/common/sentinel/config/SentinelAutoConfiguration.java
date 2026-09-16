package com.micro.cloud.common.sentinel.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.micro.cloud.common.sentinel.handler.SentinelBlockHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * common-sentinel 自动装配入口
 */
@Configuration
public class SentinelAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SentinelBlockHandler sentinelBlockHandler(ObjectMapper objectMapper) {
        return new SentinelBlockHandler(objectMapper);
    }
}
