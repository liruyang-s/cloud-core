package com.micro.cloud.file;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 文件服务
 */
@SpringBootApplication
@EnableDiscoveryClient
public class FileApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileApplication.class, args);
        System.out.println("micro-file 启动成功");
    }
}
