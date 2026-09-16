package com.micro.cloud.common.sentinel.handler;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.micro.cloud.common.core.domain.Result;
import com.micro.cloud.common.core.domain.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;

/**
 * Sentinel 限流降级统一返回处理
 */
@RequiredArgsConstructor
public class SentinelBlockHandler implements BlockExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, BlockException e) throws Exception {
        Result<Void> result;
        if (e instanceof FlowException) {
            result = Result.fail(ResultCode.TOO_MANY_REQUESTS);
        } else if (e instanceof ParamFlowException) {
            result = Result.fail(ResultCode.TOO_MANY_REQUESTS.getCode(), "热点参数限流触发");
        } else if (e instanceof DegradeException) {
            result = Result.fail(ResultCode.SERVICE_UNAVAILABLE.getCode(), "服务熔断降级，请稍后再试");
        } else if (e instanceof AuthorityException) {
            result = Result.fail(ResultCode.FORBIDDEN);
        } else if (e instanceof SystemBlockException) {
            result = Result.fail(ResultCode.SERVICE_UNAVAILABLE);
        } else {
            result = Result.fail(ResultCode.TOO_MANY_REQUESTS);
        }
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
