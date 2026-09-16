package com.micro.cloud.user.controller;

import com.micro.cloud.common.core.domain.Result;
import com.micro.cloud.user.domain.SysUser;
import com.micro.cloud.user.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户管理
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService userService;

    @Operation(summary = "用户列表（数据权限过滤）")
    @GetMapping("/list")
    public Result<List<SysUser>> list(@RequestParam(required = false) String userName,
                                      @RequestParam(required = false) String phonenumber,
                                      @RequestParam(required = false) String status,
                                      @RequestParam(required = false) Long deptId) {
        return Result.success(userService.selectUserList(userName, phonenumber, status, deptId));
    }

    @Operation(summary = "用户详情")
    @GetMapping("/{userId}")
    public Result<SysUser> getInfo(@PathVariable Long userId) {
        return Result.success(userService.getById(userId));
    }

    @Operation(summary = "新增用户")
    @PostMapping
    public Result<Void> add(@Valid @RequestBody SysUser user) {
        userService.insertUser(user);
        return Result.success();
    }

    @Operation(summary = "修改用户")
    @PutMapping
    public Result<Void> edit(@Valid @RequestBody SysUser user) {
        userService.updateUser(user);
        return Result.success();
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{userIds}")
    public Result<Void> remove(@PathVariable List<Long> userIds) {
        userService.deleteUserByIds(userIds);
        return Result.success();
    }

    @Operation(summary = "重置密码")
    @PutMapping("/resetPwd")
    public Result<Void> resetPwd(@RequestBody SysUser user) {
        userService.resetPassword(user.getUserId(), user.getPassword());
        return Result.success();
    }
}
