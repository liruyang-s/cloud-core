package com.micro.cloud.user.controller;

import com.micro.cloud.common.core.domain.Result;
import com.micro.cloud.user.domain.SysNotice;
import com.micro.cloud.user.service.SysNoticeService;
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
 * 通知公告
 */
@Tag(name = "通知公告")
@RestController
@RequestMapping("/notice")
@RequiredArgsConstructor
public class SysNoticeController {

    private final SysNoticeService noticeService;

    @Operation(summary = "公告列表")
    @GetMapping("/list")
    public Result<List<SysNotice>> list(SysNotice notice) {
        return Result.success(noticeService.selectNoticeList(notice));
    }

    @Operation(summary = "公告详情")
    @GetMapping("/{noticeId}")
    public Result<SysNotice> getInfo(@PathVariable Long noticeId) {
        return Result.success(noticeService.getById(noticeId));
    }

    @Operation(summary = "新增公告")
    @PostMapping
    public Result<Void> add(@Valid @RequestBody SysNotice notice) {
        noticeService.save(notice);
        return Result.success();
    }

    @Operation(summary = "修改公告")
    @PutMapping
    public Result<Void> edit(@Valid @RequestBody SysNotice notice) {
        noticeService.updateById(notice);
        return Result.success();
    }

    @Operation(summary = "删除公告")
    @DeleteMapping("/{noticeIds}")
    public Result<Void> remove(@PathVariable List<Long> noticeIds) {
        noticeService.removeByIds(noticeIds);
        return Result.success();
    }
}
