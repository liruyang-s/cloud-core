package com.micro.cloud.job.config;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * XXL-Job 执行器配置
 * <p>
 * 业务服务引入 micro-job-client 后自动注册执行器，
 * 只需在方法上标注 {@code @XxlJob("handlerName")} 即可编写任务逻辑。
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(XxlJobProperties.class)
public class XxlJobConfig {

    private final XxlJobProperties properties;

    @Bean
    public XxlJobSpringExecutor xxlJobExecutor() {
        log.info("XXL-Job 执行器初始化: admin={}, app={}",
                properties.getAdminAddresses(), properties.getExecutor().getAppName());
        XxlJobSpringExecutor executor = new XxlJobSpringExecutor();
        executor.setAdminAddresses(properties.getAdminAddresses());
        executor.setAccessToken(properties.getAccessToken());
        executor.setAppname(properties.getExecutor().getAppName());
        executor.setAddress(properties.getExecutor().getAddress());
        executor.setIp(properties.getExecutor().getIp());
        executor.setPort(properties.getExecutor().getPort());
        executor.setLogPath(properties.getExecutor().getLogPath());
        executor.setLogRetentionDays(properties.getExecutor().getLogRetentionDays());
        return executor;
    }
}
