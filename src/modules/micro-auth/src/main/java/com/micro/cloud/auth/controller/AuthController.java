package com.micro.cloud.auth.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import com.micro.cloud.auth.domain.LoginBody;
import com.micro.cloud.auth.service.AuthService;
import com.micro.cloud.auth.service.CaptchaService;
import com.micro.cloud.common.core.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 认证接口
 */
@Tag(name = "认证中心")
@RestController
@RequestMapping
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CaptchaService captchaService;

    @Operation(summary = "获取图形验证码")
    @GetMapping("/captcha")
    public Result<Map<String, String>> captcha() {
        return Result.success(captchaService.generate());
    }

    @Operation(summary = "账号密码登录")
    @PostMapping("/login")
    public Result<SaTokenInfo> login(@Valid @RequestBody LoginBody body, HttpServletRequest request) {
        return Result.success(authService.login(body, request));
    }

    @Operation(summary = "注销登录")
    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.success();
    }
}
