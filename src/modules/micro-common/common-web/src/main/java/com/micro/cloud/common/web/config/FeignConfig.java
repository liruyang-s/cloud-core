package com.micro.cloud.common.web.config;

import cn.dev33.satoken.same.SaSameUtil;
import cn.dev33.satoken.stp.StpUtil;
import com.micro.cloud.common.core.constant.Constants;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Feign 全局配置：跨服务调用统一透传 Token 与 TraceId
 */
@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return (RequestTemplate template) -> {
            // 透传 TraceId
            String traceId = MDC.get(Constants.TRACE_ID_KEY);
            if (traceId != null) {
                template.header(Constants.TRACE_ID_HEADER, traceId);
            }
            // 透传登录 Token
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String token = request.getHeader(StpUtil.getTokenName());
                if (token != null) {
                    template.header(StpUtil.getTokenName(), token);
                }
            }
            // Same-Token 内部调用认证（防伪造内部接口调用）
            template.header(SaSameUtil.SAME_TOKEN, SaSameUtil.getToken());
        };
    }
}
