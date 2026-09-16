package com.micro.cloud.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.micro.cloud.user.domain.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * 菜单权限 Mapper
 */
@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    /**
     * 根据用户ID查询菜单列表（目录 + 菜单）
     */
    List<SysMenu> selectMenusByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID查询权限标识集合
     */
    Set<String> selectPermsByUserId(@Param("userId") Long userId);

    /**
     * 根据角色ID查询菜单ID列表
     */
    List<Long> selectMenuIdsByRoleId(@Param("roleId") Long roleId);
}
