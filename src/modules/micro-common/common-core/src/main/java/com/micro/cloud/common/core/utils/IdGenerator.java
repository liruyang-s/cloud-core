package com.micro.cloud.common.core.utils;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

/**
 * 分布式 ID 生成器（雪花算法）
 * <p>
 * 统一业务主键、流水号生成方案。机器 ID 通过 Nacos 配置中心或环境变量注入，
 * 避免多实例部署时 ID 冲突。
 * <p>
 * 配置项：
 * <pre>
 * id:
 *   worker-id: 1      # 工作机器 ID（0-31），各实例必须唯一
 *   datacenter-id: 1  # 数据中心 ID（0-31）
 * </pre>
 * 使用方式：注入 {@code IdGenerator} 后调用 {@link #nextId()} 或 {@link #nextIdStr()}。
 */
@Slf4j
public class IdGenerator {

    /** 工作机器 ID（0-31） */
    @Value("${id.worker-id:1}")
    private long workerId;

    /** 数据中心 ID（0-31） */
    @Value("${id.datacenter-id:1}")
    private long datacenterId;

    private Snowflake snowflake;

    @PostConstruct
    public void init() {
        this.snowflake = IdUtil.getSnowflake(workerId, datacenterId);
        log.info("分布式 ID 生成器初始化完成: workerId={}, datacenterId={}", workerId, datacenterId);
    }

    /**
     * 生成下一个分布式 ID（long 型）
     */
    public long nextId() {
        return snowflake.nextId();
    }

    /**
     * 生成下一个分布式 ID（字符串）
     */
    public String nextIdStr() {
        return snowflake.nextIdStr();
    }
}
