package com.micro.cloud.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.micro.cloud.user.domain.SysUserPost;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户岗位关联 Mapper
 */
@Mapper
public interface SysUserPostMapper extends BaseMapper<SysUserPost> {
}
