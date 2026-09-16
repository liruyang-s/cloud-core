package com.micro.cloud.user.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.micro.cloud.common.core.domain.Result;
import com.micro.cloud.user.domain.SysDept;
import com.micro.cloud.user.service.SysDeptService;
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
 * 部门管理
 */
@Tag(name = "部门管理")
@RestController
@RequestMapping("/dept")
@RequiredArgsConstructor
public class SysDeptController {

    private final SysDeptService deptService;

    @Operation(summary = "部门列表")
    @SaCheckPermission("system:dept:list")
    @GetMapping("/list")
    public Result<List<SysDept>> list(SysDept dept) {
        return Result.success(deptService.selectDeptList(dept));
    }

    @Operation(summary = "部门树")
    @SaCheckPermission("system:dept:list")
    @GetMapping("/tree")
    public Result<List<SysDept>> tree(SysDept dept) {
        return Result.success(deptService.buildDeptTree(deptService.selectDeptList(dept)));
    }

    @Operation(summary = "部门详情")
    @SaCheckPermission("system:dept:query")
    @GetMapping("/{deptId}")
    public Result<SysDept> getInfo(@PathVariable Long deptId) {
        return Result.success(deptService.getById(deptId));
    }

    @Operation(summary = "新增部门")
    @SaCheckPermission("system:dept:add")
    @PostMapping
    public Result<Void> add(@Valid @RequestBody SysDept dept) {
        deptService.insertDept(dept);
        return Result.success();
    }

    @Operation(summary = "修改部门")
    @SaCheckPermission("system:dept:edit")
    @PutMapping
    public Result<Void> edit(@Valid @RequestBody SysDept dept) {
        deptService.updateDept(dept);
        return Result.success();
    }

    @Operation(summary = "删除部门")
    @SaCheckPermission("system:dept:remove")
    @DeleteMapping("/{deptId}")
    public Result<Void> remove(@PathVariable Long deptId) {
        deptService.removeById(deptId);
        return Result.success();
    }
}
