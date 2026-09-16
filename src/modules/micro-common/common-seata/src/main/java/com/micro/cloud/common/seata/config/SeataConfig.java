package com.micro.cloud.common.seata.config;

import io.seata.spring.annotation.GlobalTransactionScanner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Seata 分布式事务配置
 * <p>
 * 底座只负责集成 Seata 框架与全局事务扫描器，业务事务逻辑由业务服务自行编写。
 * 业务方法标注 {@code @GlobalTransactional} 即可开启跨服务分布式事务（AT 模式）。
 */
@Configuration
public class SeataConfig {

    /** 应用名（事务分组定位用，默认取服务名） */
    @Value("${spring.application.name:application}")
    private String applicationName;

    /** Seata 事务分组，需与 seata-server 的 service.vgroupMapping 一致 */
    @Value("${seata.tx-service-group:${spring.application.name}-group}")
    private String txServiceGroup;

    /**
     * 全局事务扫描器：拦截 @GlobalTransactional 注解
     */
    @Bean
    @ConditionalOnMissingBean
    public GlobalTransactionScanner globalTransactionScanner() {
        return new GlobalTransactionScanner(applicationName, txServiceGroup);
    }
}
