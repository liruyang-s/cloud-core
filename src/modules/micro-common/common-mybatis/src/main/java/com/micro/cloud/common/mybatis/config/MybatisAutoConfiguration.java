package com.micro.cloud.common.mybatis.config;

import com.micro.cloud.common.mybatis.handler.AutoFillMetaObjectHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * common-mybatis 自动装配入口
 */
@Configuration
@Import(MybatisPlusConfig.class)
public class MybatisAutoConfiguration {

    @Bean
    public AutoFillMetaObjectHandler autoFillMetaObjectHandler() {
        return new AutoFillMetaObjectHandler();
    }
}
