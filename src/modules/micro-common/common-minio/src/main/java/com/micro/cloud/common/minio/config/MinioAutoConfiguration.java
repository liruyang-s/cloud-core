package com.micro.cloud.common.minio.config;

import com.micro.cloud.common.minio.utils.MinioUtils;
import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * common-minio 自动装配入口
 */
@Configuration
@Import(MinioConfig.class)
public class MinioAutoConfiguration {

    @Bean
    public MinioUtils minioUtils(MinioClient minioClient, MinioProperties properties) {
        return new MinioUtils(minioClient, properties);
    }
}
