package com.micro.cloud.message;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 消息服务
 */
@SpringBootApplication
@EnableDiscoveryClient
public class MessageApplication {

    public static void main(String[] args) {
        SpringApplication.run(MessageApplication.class, args);
        System.out.println("micro-message 启动成功");
    }
}
