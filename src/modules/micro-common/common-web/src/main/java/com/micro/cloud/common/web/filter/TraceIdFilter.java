package com.micro.cloud.common.web.filter;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.micro.cloud.common.core.constant.Constants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * TraceId 链路追踪过滤器
 * <p>
 * 从请求头获取网关透传的 X-Trace-Id，无则自动生成，写入 MDC 贯穿整条日志链路。
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class TraceIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String traceId = request.getHeader(Constants.TRACE_ID_HEADER);
        if (StrUtil.isBlank(traceId)) {
            traceId = IdUtil.fastSimpleUUID();
        }
        MDC.put(Constants.TRACE_ID_KEY, traceId);
        response.setHeader(Constants.TRACE_ID_HEADER, traceId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(Constants.TRACE_ID_KEY);
        }
    }
}
