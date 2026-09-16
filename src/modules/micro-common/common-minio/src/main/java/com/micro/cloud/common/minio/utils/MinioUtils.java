package com.micro.cloud.common.minio.utils;

import com.micro.cloud.common.core.exception.ServiceException;
import com.micro.cloud.common.minio.config.MinioProperties;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;

/**
 * MinIO 文件存储工具：上传、下载、删除
 */
@Slf4j
@RequiredArgsConstructor
public class MinioUtils {

    private final MinioClient minioClient;
    private final MinioProperties properties;

    /**
     * 上传文件
     *
     * @param objectName  对象名（含路径前缀，如 avatar/xxx.png）
     * @param inputStream 文件流
     * @param size        文件大小
     * @param contentType 内容类型
     */
    public void upload(String objectName, InputStream inputStream, long size, String contentType) {
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(properties.getBucket())
                    .object(objectName)
                    .stream(inputStream, size, -1)
                    .contentType(contentType)
                    .build());
        } catch (Exception e) {
            log.error("文件上传失败: {}", objectName, e);
            throw new ServiceException("文件上传失败");
        }
    }

    /** 下载文件流 */
    public InputStream download(String objectName) {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(properties.getBucket())
                    .object(objectName)
                    .build());
        } catch (Exception e) {
            log.error("文件下载失败: {}", objectName, e);
            throw new ServiceException("文件下载失败");
        }
    }

    /** 删除文件 */
    public void remove(String objectName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(properties.getBucket())
                    .object(objectName)
                    .build());
        } catch (Exception e) {
            log.error("文件删除失败: {}", objectName, e);
            throw new ServiceException("文件删除失败");
        }
    }

    /** 获取文件访问地址 */
    public String getFileUrl(String objectName) {
        return properties.getEndpoint() + "/" + properties.getBucket() + "/" + objectName;
    }
}
