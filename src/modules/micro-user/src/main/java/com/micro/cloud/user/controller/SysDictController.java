package com.micro.cloud.user.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.micro.cloud.common.core.domain.Result;
import com.micro.cloud.user.domain.SysDictData;
import com.micro.cloud.user.domain.SysDictType;
import com.micro.cloud.user.service.SysDictService;
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
 * 字典管理
 */
@Tag(name = "字典管理")
@RestController
@RequestMapping("/dict")
@RequiredArgsConstructor
public class SysDictController {

    private final SysDictService dictService;

    @Operation(summary = "字典类型列表")
    @SaCheckPermission("system:dict:list")
    @GetMapping("/type/list")
    public Result<List<SysDictType>> typeList(SysDictType dictType) {
        return Result.success(dictService.selectDictTypeList(dictType));
    }

    @Operation(summary = "字典类型详情")
    @SaCheckPermission("system:dict:query")
    @GetMapping("/type/{dictId}")
    public Result<SysDictType> typeInfo(@PathVariable Long dictId) {
        return Result.success(dictService.getById(dictId));
    }

    @Operation(summary = "新增字典类型")
    @SaCheckPermission("system:dict:add")
    @PostMapping("/type")
    public Result<Void> addType(@Valid @RequestBody SysDictType dictType) {
        dictService.save(dictType);
        return Result.success();
    }

    @Operation(summary = "修改字典类型")
    @SaCheckPermission("system:dict:edit")
    @PutMapping("/type")
    public Result<Void> editType(@Valid @RequestBody SysDictType dictType) {
        dictService.updateById(dictType);
        return Result.success();
    }

    @Operation(summary = "删除字典类型")
    @SaCheckPermission("system:dict:remove")
    @DeleteMapping("/type/{dictIds}")
    public Result<Void> removeType(@PathVariable List<Long> dictIds) {
        dictService.removeByIds(dictIds);
        return Result.success();
    }

    @Operation(summary = "根据字典类型查询字典数据（前端下拉回显，登录即可）")
    @GetMapping("/data/type/{dictType}")
    public Result<List<SysDictData>> dataByType(@PathVariable String dictType) {
        return Result.success(dictService.selectDictDataByType(dictType));
    }

    @Operation(summary = "新增字典数据")
    @SaCheckPermission("system:dict:add")
    @PostMapping("/data")
    public Result<Void> addData(@Valid @RequestBody SysDictData dictData) {
        dictService.insertDictData(dictData);
        return Result.success();
    }

    @Operation(summary = "修改字典数据")
    @SaCheckPermission("system:dict:edit")
    @PutMapping("/data")
    public Result<Void> editData(@Valid @RequestBody SysDictData dictData) {
        dictService.updateDictData(dictData);
        return Result.success();
    }

    @Operation(summary = "删除字典数据")
    @SaCheckPermission("system:dict:remove")
    @DeleteMapping("/data/{dictCode}")
    public Result<Void> removeData(@PathVariable Long dictCode) {
        dictService.deleteDictData(dictCode);
        return Result.success();
    }
}
