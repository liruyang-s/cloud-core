package com.micro.cloud.common.web.aspect;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.micro.cloud.common.core.exception.ServiceException;
import com.micro.cloud.common.web.annotation.RepeatSubmit;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;

/**
 * 防重复提交切面
 * <p>
 * 拦截标注 {@link RepeatSubmit} 的 Controller 方法，基于 Redis SETNX
 * 在时间窗口内拒绝重复请求。未引入 Redis 时切面自动放行。
 */
@Slf4j
@Aspect
@Component
@ConditionalOnClass(name = "org.springframework.data.redis.core.StringRedisTemplate")
public class RepeatSubmitAspect {

    private static final String REPEAT_SUBMIT_KEY = "repeat_submit:";

    private final ObjectProvider<StringRedisTemplate> redisTemplateProvider;

    public RepeatSubmitAspect(ObjectProvider<StringRedisTemplate> redisTemplateProvider) {
        this.redisTemplateProvider = redisTemplateProvider;
    }

    @Around("@annotation(repeatSubmit)")
    public Object around(ProceedingJoinPoint point, RepeatSubmit repeatSubmit) throws Throwable {
        StringRedisTemplate redisTemplate = redisTemplateProvider.getIfAvailable();
        if (redisTemplate == null) {
            log.warn("未配置 Redis，防重复提交不生效");
            return point.proceed();
        }
        String key = buildKey();
        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(key, "1", Duration.ofMillis(repeatSubmit.interval()));
        if (Boolean.FALSE.equals(success)) {
            log.warn("重复提交被拦截: key={}", key);
            throw new ServiceException(repeatSubmit.message());
        }
        return point.proceed();
    }

    /**
     * 构建防重 key：请求路径 + 用户标识
     */
    private String buildKey() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String uri = "";
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            uri = request.getRequestURI();
        }
        String userId;
        try {
            userId = String.valueOf(StpUtil.getLoginId());
        } catch (Exception e) {
            userId = "anonymous";
        }
        return REPEAT_SUBMIT_KEY + userId + ":" + StrUtil.replace(uri, "/", ":");
    }
}
