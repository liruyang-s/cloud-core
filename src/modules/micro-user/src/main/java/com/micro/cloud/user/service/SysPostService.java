package com.micro.cloud.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.micro.cloud.user.domain.SysPost;
import com.micro.cloud.user.mapper.SysPostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 岗位服务
 */
@Service
@RequiredArgsConstructor
public class SysPostService extends ServiceImpl<SysPostMapper, SysPost> {

    /** 岗位列表 */
    public List<SysPost> selectPostList(SysPost post) {
        LambdaQueryWrapper<SysPost> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(post.getPostName() != null, SysPost::getPostName, post.getPostName())
                .like(post.getPostCode() != null, SysPost::getPostCode, post.getPostCode())
                .eq(post.getStatus() != null, SysPost::getStatus, post.getStatus())
                .orderByAsc(SysPost::getPostSort);
        return list(wrapper);
    }
}
