package com.micro.cloud.common.minio.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * MinIO 配置属性（敏感信息通过环境变量注入）
 */
@Data
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {

    /** MinIO 服务地址 */
    private String endpoint;

    /** 访问密钥 */
    private String accessKey;

    /** 密钥 */
    private String secretKey;

    /** 默认存储桶 */
    private String bucket = "micro-cloud";
}
