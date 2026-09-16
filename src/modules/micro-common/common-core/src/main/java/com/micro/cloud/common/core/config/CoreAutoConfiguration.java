package com.micro.cloud.common.core.config;

import com.micro.cloud.common.core.utils.AlertUtils;
import com.micro.cloud.common.core.utils.IdGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * common-core 自动装配入口
 * <p>
 * 注册分布式 ID 生成器与钉钉告警工具，业务服务引入 common-core 即可直接使用。
 */
@AutoConfiguration
public class CoreAutoConfiguration {

    @Bean
    public IdGenerator idGenerator() {
        return new IdGenerator();
    }

    @Bean
    public AlertUtils alertUtils(@Value("${alert.dingtalk.webhook:}") String webhook,
                                 @Value("${alert.dingtalk.secret:}") String secret) {
        return new AlertUtils(webhook, secret);
    }
}
