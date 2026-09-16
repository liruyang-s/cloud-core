package com.micro.cloud.user.controller;

import com.micro.cloud.common.core.domain.Result;
import com.micro.cloud.common.mybatis.core.LoginUser;
import com.micro.cloud.user.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 内部接口（仅供其他微服务通过 Feign 调用，网关不对外暴露 /inner/**）
 */
@RestController
@RequestMapping("/inner")
@RequiredArgsConstructor
public class InnerUserController {

    private final SysUserService userService;

    /**
     * 认证中心获取登录用户信息（含角色、权限、数据范围）
     */
    @GetMapping("/user/info/{username}")
    public Result<LoginUser> getUserInfo(@PathVariable("username") String username) {
        return Result.success(userService.getLoginUser(username));
    }
}
