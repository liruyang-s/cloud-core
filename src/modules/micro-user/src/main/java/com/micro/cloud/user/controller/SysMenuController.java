package com.micro.cloud.user.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.micro.cloud.common.core.domain.Result;
import com.micro.cloud.user.domain.SysMenu;
import com.micro.cloud.user.service.SysMenuService;
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
 * 菜单管理
 */
@Tag(name = "菜单管理")
@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
public class SysMenuController {

    private final SysMenuService menuService;

    @Operation(summary = "菜单列表")
    @GetMapping("/list")
    public Result<List<SysMenu>> list(SysMenu menu) {
        return Result.success(menuService.selectMenuList(menu));
    }

    @Operation(summary = "菜单树")
    @GetMapping("/tree")
    public Result<List<SysMenu>> tree(SysMenu menu) {
        return Result.success(menuService.buildMenuTree(menuService.selectMenuList(menu)));
    }

    @Operation(summary = "当前用户路由菜单")
    @GetMapping("/routers")
    public Result<List<SysMenu>> routers() {
        Long userId = StpUtil.getLoginIdAsLong();
        return Result.success(menuService.selectMenusByUserId(userId));
    }

    @Operation(summary = "菜单详情")
    @GetMapping("/{menuId}")
    public Result<SysMenu> getInfo(@PathVariable Long menuId) {
        return Result.success(menuService.getById(menuId));
    }

    @Operation(summary = "新增菜单")
    @PostMapping
    public Result<Void> add(@Valid @RequestBody SysMenu menu) {
        menuService.save(menu);
        return Result.success();
    }

    @Operation(summary = "修改菜单")
    @PutMapping
    public Result<Void> edit(@Valid @RequestBody SysMenu menu) {
        menuService.updateById(menu);
        return Result.success();
    }

    @Operation(summary = "删除菜单")
    @DeleteMapping("/{menuId}")
    public Result<Void> remove(@PathVariable Long menuId) {
        menuService.removeById(menuId);
        return Result.success();
    }
}
