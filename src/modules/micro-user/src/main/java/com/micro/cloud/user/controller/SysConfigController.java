package com.micro.cloud.user.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.micro.cloud.common.core.domain.Result;
import com.micro.cloud.user.domain.SysConfig;
import com.micro.cloud.user.service.SysConfigService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 参数配置
 */
@Tag(name = "参数配置")
@RestController
@RequestMapping("/config")
@RequiredArgsConstructor
public class SysConfigController {

    private final SysConfigService configService;

    @Operation(summary = "参数列表")
    @SaCheckPermission("system:config:list")
    @GetMapping("/list")
    public Result<List<SysConfig>> list(SysConfig config) {
        return Result.success(configService.selectConfigList(config));
    }

    @Operation(summary = "参数详情")
    @SaCheckPermission("system:config:query")
    @GetMapping("/{configId}")
    public Result<SysConfig> getInfo(@PathVariable Long configId) {
        return Result.success(configService.getById(configId));
    }

    @Operation(summary = "根据键名查询参数值")
    @GetMapping("/configKey/{configKey}")
    public Result<String> configKey(@PathVariable String configKey) {
        return Result.success(configService.selectConfigByKey(configKey));
    }

    @Operation(summary = "新增参数")
    @SaCheckPermission("system:config:add")
    @PostMapping
    public Result<Void> add(@Valid @RequestBody SysConfig config) {
        configService.insertConfig(config);
        return Result.success();
    }

    @Operation(summary = "修改参数")
    @SaCheckPermission("system:config:edit")
    @PutMapping
    public Result<Void> edit(@Valid @RequestBody SysConfig config) {
        configService.updateConfig(config);
        return Result.success();
    }

    @Operation(summary = "删除参数")
    @SaCheckPermission("system:config:remove")
    @DeleteMapping("/{configIds}")
    public Result<Void> remove(@PathVariable List<Long> configIds) {
        configService.deleteConfigByIds(configIds);
        return Result.success();
    }
}
