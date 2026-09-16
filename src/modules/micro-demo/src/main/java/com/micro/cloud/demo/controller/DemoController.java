package com.micro.cloud.demo.controller;

import com.micro.cloud.common.core.domain.Result;
import com.micro.cloud.common.redis.utils.CacheUtils;
import com.micro.cloud.common.redis.utils.DistributedLockUtils;
import com.micro.cloud.demo.service.SeataDemoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 演示接口：两级缓存、分布式锁、链路追踪、限流熔断
 */
@Slf4j
@Tag(name = "演示接口")
@RestController
@RequestMapping("/demo")
@RequiredArgsConstructor
public class DemoController {

    private final CacheUtils cacheUtils;
    private final DistributedLockUtils lockUtils;
    private final SeataDemoService seataDemoService;

    private final AtomicInteger counter = new AtomicInteger();

    @Operation(summary = "健康检查")
    @GetMapping("/ping")
    public Result<String> ping() {
        return Result.success("pong " + LocalDateTime.now());
    }

    @Operation(summary = "两级缓存示例（Caffeine + Redis）")
    @GetMapping("/cache")
    public Result<Map<String, Object>> cache() {
        long start = System.currentTimeMillis();
        String value = cacheUtils.get("demo:cache:key", 60, () -> "从数据库加载的热点数据");
        Map<String, Object> data = new HashMap<>();
        data.put("value", value);
        data.put("costMs", System.currentTimeMillis() - start);
        return Result.success(data);
    }

    @Operation(summary = "分布式锁示例（模拟库存扣减）")
    @GetMapping("/lock")
    public Result<Integer> lock() {
        int stock = lockUtils.executeWithLock("demo:stock", 3, 10, () -> {
            int current = counter.incrementAndGet();
            log.info("扣减库存成功, 当前计数: {}", current);
            return current;
        });
        return Result.success(stock);
    }

    @Operation(summary = "链路追踪示例（查看日志中的 traceId）")
    @GetMapping("/trace")
    public Result<String> trace() {
        log.info("这是一条带链路追踪ID的日志");
        return Result.success("traceId=" + MDC.get("traceId"));
    }

    @Operation(summary = "限流熔断示例（在 Sentinel 控制台配置规则后测试）")
    @GetMapping("/sentinel")
    public Result<String> sentinel() {
        return Result.success("sentinel ok");
    }

    @Operation(summary = "Seata 分布式事务示例（跨服务事务骨架）")
    @GetMapping("/seata")
    public Result<String> seata() {
        return Result.success(seataDemoService.demoGlobalTransaction());
    }
}
