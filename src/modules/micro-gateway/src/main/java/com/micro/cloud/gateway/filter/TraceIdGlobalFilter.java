package com.micro.cloud.gateway.filter;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * TraceId 全链路追踪过滤器
 * <p>
 * 网关作为流量入口生成唯一 TraceId：
 * 1. 写入请求头 X-Trace-Id 透传给下游服务；
 * 2. 写入 Reactor Context，借助 context-propagation 桥接日志 MDC。
 */
@Component
public class TraceIdGlobalFilter implements GlobalFilter, Ordered {

    public static final String TRACE_ID_HEADER = "X-Trace-Id";
    public static final String TRACE_ID_KEY = "traceId";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String traceId = exchange.getRequest().getHeaders().getFirst(TRACE_ID_HEADER);
        if (StrUtil.isBlank(traceId)) {
            traceId = IdUtil.fastSimpleUUID();
        }
        ServerHttpRequest request = exchange.getRequest().mutate()
                .header(TRACE_ID_HEADER, traceId)
                .build();
        String finalTraceId = traceId;
        MDC.put(TRACE_ID_KEY, traceId);
        return chain.filter(exchange.mutate().request(request).build())
                .contextWrite(ctx -> ctx.put(TRACE_ID_KEY, finalTraceId))
                .doFinally(signal -> MDC.remove(TRACE_ID_KEY));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
