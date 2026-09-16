package com.micro.cloud.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.micro.cloud.user.domain.SysRoleMenu;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色菜单关联 Mapper
 */
@Mapper
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {
}
