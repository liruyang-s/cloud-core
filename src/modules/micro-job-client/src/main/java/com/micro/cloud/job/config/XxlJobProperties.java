package com.micro.cloud.job.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * XXL-Job 客户端配置属性
 * <p>
 * 配置项（建议放 Nacos 配置中心，通过环境变量注入）：
 * <pre>
 * xxl:
 *   job:
 *     admin-addresses: http://127.0.0.1:8081/xxl-job-admin
 *     access-token: xxx
 *     executor:
 *       app-name: ${spring.application.name}
 *       port: 9999
 * </pre>
 */
@Data
@ConfigurationProperties(prefix = "xxl.job")
public class XxlJobProperties {

    /** 调度中心地址，多个用逗号分隔（集群） */
    private String adminAddresses;

    /** 调度中心通讯 token（与调度中心配置一致） */
    private String accessToken;

    /** 执行器配置 */
    private Executor executor = new Executor();

    @Data
    public static class Executor {
        /** 执行器 AppName（调度中心分组依据） */
        private String appName;
        /** 执行器注册地址（默认自动获取） */
        private String address;
        /** 执行器 IP（默认自动获取） */
        private String ip;
        /** 执行器端口号 */
        private int port = 9999;
        /** 执行器日志路径 */
        private String logPath = "logs/xxl-job";
        /** 执行器日志保留天数（-1 永久） */
        private int logRetentionDays = 30;
    }
}
