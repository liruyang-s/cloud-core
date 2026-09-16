package com.micro.cloud.user.controller;

import com.micro.cloud.common.core.domain.Result;
import com.micro.cloud.user.domain.SysLoginLog;
import com.micro.cloud.user.domain.SysOperLog;
import com.micro.cloud.user.service.SysLoginLogService;
import com.micro.cloud.user.service.SysOperLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 系统日志（操作日志 + 登录日志）
 */
@Tag(name = "系统日志")
@RestController
@RequestMapping("/log")
@RequiredArgsConstructor
public class SysLogController {

    private final SysOperLogService operLogService;
    private final SysLoginLogService loginLogService;

    @Operation(summary = "操作日志列表")
    @GetMapping("/oper/list")
    public Result<List<SysOperLog>> operList(SysOperLog operLog) {
        return Result.success(operLogService.selectOperLogList(operLog));
    }

    @Operation(summary = "删除操作日志")
    @DeleteMapping("/oper/{operIds}")
    public Result<Void> removeOper(@PathVariable List<Long> operIds) {
        operLogService.removeByIds(operIds);
        return Result.success();
    }

    @Operation(summary = "清空操作日志")
    @DeleteMapping("/oper/clean")
    public Result<Void> cleanOper() {
        operLogService.cleanOperLog();
        return Result.success();
    }

    @Operation(summary = "登录日志列表")
    @GetMapping("/login/list")
    public Result<List<SysLoginLog>> loginList(SysLoginLog loginLog) {
        return Result.success(loginLogService.selectLoginLogList(loginLog));
    }

    @Operation(summary = "删除登录日志")
    @DeleteMapping("/login/{infoIds}")
    public Result<Void> removeLogin(@PathVariable List<Long> infoIds) {
        loginLogService.removeByIds(infoIds);
        return Result.success();
    }

    @Operation(summary = "清空登录日志")
    @DeleteMapping("/login/clean")
    public Result<Void> cleanLogin() {
        loginLogService.cleanLoginLog();
        return Result.success();
    }
}
