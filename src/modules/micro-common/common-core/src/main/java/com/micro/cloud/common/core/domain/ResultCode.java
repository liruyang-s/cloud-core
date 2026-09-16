package com.micro.cloud.common.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 统一响应状态码
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    FAIL(500, "系统异常"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "没有权限访问"),
    NOT_FOUND(404, "资源不存在"),
    PARAM_ERROR(400, "参数校验失败"),
    TOO_MANY_REQUESTS(429, "请求过于频繁，请稍后再试"),
    SERVICE_UNAVAILABLE(503, "服务暂不可用");

    private final int code;
    private final String msg;
}
