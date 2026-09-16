package com.micro.cloud.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.micro.cloud.user.domain.SysUserRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户角色关联 Mapper
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {
}
