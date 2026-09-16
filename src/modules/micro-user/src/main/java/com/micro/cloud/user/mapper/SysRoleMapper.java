package com.micro.cloud.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.micro.cloud.user.domain.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * 角色信息 Mapper
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

    /**
     * 根据用户ID查询角色列表
     */
    List<SysRole> selectRolesByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID查询角色权限字符串
     */
    Set<String> selectRoleKeysByUserId(@Param("userId") Long userId);
}
