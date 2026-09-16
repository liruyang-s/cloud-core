package com.micro.cloud.user.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.micro.cloud.common.core.domain.Result;
import com.micro.cloud.user.domain.SysRole;
import com.micro.cloud.user.service.SysRoleService;
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
 * 角色管理
 */
@Tag(name = "角色管理")
@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
public class SysRoleController {

    private final SysRoleService roleService;

    @Operation(summary = "角色列表")
    @SaCheckPermission("system:role:list")
    @GetMapping("/list")
    public Result<List<SysRole>> list(SysRole role) {
        return Result.success(roleService.selectRoleList(role));
    }

    @Operation(summary = "角色详情")
    @SaCheckPermission("system:role:query")
    @GetMapping("/{roleId}")
    public Result<SysRole> getInfo(@PathVariable Long roleId) {
        return Result.success(roleService.getById(roleId));
    }

    @Operation(summary = "角色已分配菜单ID")
    @SaCheckPermission("system:role:query")
    @GetMapping("/menuIds/{roleId}")
    public Result<List<Long>> menuIds(@PathVariable Long roleId) {
        return Result.success(roleService.selectMenuIdsByRoleId(roleId));
    }

    @Operation(summary = "新增角色")
    @SaCheckPermission("system:role:add")
    @PostMapping
    public Result<Void> add(@Valid @RequestBody SysRole role) {
        roleService.insertRole(role);
        return Result.success();
    }

    @Operation(summary = "修改角色")
    @SaCheckPermission("system:role:edit")
    @PutMapping
    public Result<Void> edit(@Valid @RequestBody SysRole role) {
        roleService.updateRole(role);
        return Result.success();
    }

    @Operation(summary = "删除角色")
    @SaCheckPermission("system:role:remove")
    @DeleteMapping("/{roleIds}")
    public Result<Void> remove(@PathVariable List<Long> roleIds) {
        roleService.deleteRoleByIds(roleIds);
        return Result.success();
    }
}
