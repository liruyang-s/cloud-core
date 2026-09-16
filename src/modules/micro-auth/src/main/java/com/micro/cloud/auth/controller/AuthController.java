package com.micro.cloud.auth.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import com.micro.cloud.auth.domain.LoginBody;
import com.micro.cloud.auth.service.AuthService;
import com.micro.cloud.common.core.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证接口
 */
@Tag(name = "认证中心")
@RestController
@RequestMapping
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "账号密码登录")
    @PostMapping("/login")
    public Result<SaTokenInfo> login(@Valid @RequestBody LoginBody body) {
        return Result.success(authService.login(body));
    }

    @Operation(summary = "注销登录")
    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.success();
    }
}
