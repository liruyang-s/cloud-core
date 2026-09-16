package com.micro.cloud.common.minio.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MinIO 客户端配置
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(MinioProperties.class)
public class MinioConfig {

    private final MinioProperties properties;

    @Bean
    public MinioClient minioClient() {
        MinioClient client = MinioClient.builder()
                .endpoint(properties.getEndpoint())
                .credentials(properties.getAccessKey(), properties.getSecretKey())
                .build();
        ensureBucketExists(client, properties.getBucket());
        return client;
    }

    /** 确保默认存储桶存在 */
    private void ensureBucketExists(MinioClient client, String bucket) {
        try {
            boolean exists = client.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!exists) {
                client.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
                log.info("创建 MinIO 存储桶成功: {}", bucket);
            }
        } catch (Exception e) {
            log.error("MinIO 存储桶初始化失败: {}", e.getMessage());
        }
    }
}
