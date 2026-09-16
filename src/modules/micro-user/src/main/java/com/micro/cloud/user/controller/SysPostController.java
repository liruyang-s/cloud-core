package com.micro.cloud.user.controller;

import com.micro.cloud.common.core.domain.Result;
import com.micro.cloud.user.domain.SysPost;
import com.micro.cloud.user.service.SysPostService;
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
 * 岗位管理
 */
@Tag(name = "岗位管理")
@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class SysPostController {

    private final SysPostService postService;

    @Operation(summary = "岗位列表")
    @GetMapping("/list")
    public Result<List<SysPost>> list(SysPost post) {
        return Result.success(postService.selectPostList(post));
    }

    @Operation(summary = "岗位详情")
    @GetMapping("/{postId}")
    public Result<SysPost> getInfo(@PathVariable Long postId) {
        return Result.success(postService.getById(postId));
    }

    @Operation(summary = "新增岗位")
    @PostMapping
    public Result<Void> add(@Valid @RequestBody SysPost post) {
        postService.save(post);
        return Result.success();
    }

    @Operation(summary = "修改岗位")
    @PutMapping
    public Result<Void> edit(@Valid @RequestBody SysPost post) {
        postService.updateById(post);
        return Result.success();
    }

    @Operation(summary = "删除岗位")
    @DeleteMapping("/{postIds}")
    public Result<Void> remove(@PathVariable List<Long> postIds) {
        postService.removeByIds(postIds);
        return Result.success();
    }
}
