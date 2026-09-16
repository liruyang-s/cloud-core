package com.micro.cloud.common.web.config;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import com.micro.cloud.common.core.domain.Result;
import com.micro.cloud.common.core.domain.ResultCode;
import com.micro.cloud.common.core.exception.ServiceException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * <p>
 * 统一拦截业务异常、参数异常、鉴权异常与系统异常，屏蔽底层报错细节，防止敏感信息泄露。
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 业务异常 */
    @ExceptionHandler(ServiceException.class)
    public Result<Void> handleServiceException(ServiceException e) {
        log.warn("业务异常: {}", e.getMessage());
        return Result.fail(e.getCode(), e.getMessage());
    }

    /** 未登录 / 登录过期 */
    @ExceptionHandler(NotLoginException.class)
    public Result<Void> handleNotLogin(NotLoginException e) {
        return Result.fail(ResultCode.UNAUTHORIZED);
    }

    /** 无权限 */
    @ExceptionHandler(NotPermissionException.class)
    public Result<Void> handleNotPermission(NotPermissionException e) {
        return Result.fail(ResultCode.FORBIDDEN);
    }

    /** 参数校验异常（@RequestBody） */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidException(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return Result.fail(ResultCode.PARAM_ERROR.getCode(), msg);
    }

    /** 参数绑定异常 */
    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException e) {
        String msg = e.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return Result.fail(ResultCode.PARAM_ERROR.getCode(), msg);
    }

    /** 兜底异常：屏蔽底层报错信息 */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e, HttpServletRequest request) {
        log.error("系统异常, uri={}", request.getRequestURI(), e);
        return Result.fail(ResultCode.FAIL);
    }
}
