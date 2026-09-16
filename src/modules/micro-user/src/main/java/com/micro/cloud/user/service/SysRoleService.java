package com.micro.cloud.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.micro.cloud.common.core.exception.ServiceException;
import com.micro.cloud.user.domain.SysRole;
import com.micro.cloud.user.domain.SysRoleDept;
import com.micro.cloud.user.domain.SysRoleMenu;
import com.micro.cloud.user.mapper.SysRoleDeptMapper;
import com.micro.cloud.user.mapper.SysRoleMapper;
import com.micro.cloud.user.mapper.SysRoleMenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 角色服务
 */
@Service
@RequiredArgsConstructor
public class SysRoleService extends ServiceImpl<SysRoleMapper, SysRole> {

    private final SysRoleMenuMapper roleMenuMapper;
    private final SysRoleDeptMapper roleDeptMapper;

    /** 角色列表 */
    public List<SysRole> selectRoleList(SysRole role) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(role.getRoleName() != null, SysRole::getRoleName, role.getRoleName())
                .like(role.getRoleKey() != null, SysRole::getRoleKey, role.getRoleKey())
                .eq(role.getStatus() != null, SysRole::getStatus, role.getStatus())
                .orderByAsc(SysRole::getRoleSort);
        return list(wrapper);
    }

    /** 新增角色（含菜单、部门权限分配） */
    @Transactional(rollbackFor = Exception.class)
    public void insertRole(SysRole role) {
        save(role);
        insertRoleMenu(role.getRoleId(), role.getMenuIds());
        insertRoleDept(role.getRoleId(), role.getDeptIds());
    }

    /** 修改角色（含菜单、部门权限分配） */
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(SysRole role) {
        updateById(role);
        roleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, role.getRoleId()));
        insertRoleMenu(role.getRoleId(), role.getMenuIds());
        roleDeptMapper.delete(new LambdaQueryWrapper<SysRoleDept>().eq(SysRoleDept::getRoleId, role.getRoleId()));
        insertRoleDept(role.getRoleId(), role.getDeptIds());
    }

    /** 删除角色 */
    @Transactional(rollbackFor = Exception.class)
    public void deleteRoleByIds(List<Long> roleIds) {
        removeByIds(roleIds);
        for (Long roleId : roleIds) {
            roleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));
            roleDeptMapper.delete(new LambdaQueryWrapper<SysRoleDept>().eq(SysRoleDept::getRoleId, roleId));
        }
    }

    /** 查询角色已分配的菜单ID */
    public List<Long> selectMenuIdsByRoleId(Long roleId) {
        return roleMenuMapper.selectList(new LambdaQueryWrapper<SysRoleMenu>()
                .eq(SysRoleMenu::getRoleId, roleId)).stream().map(SysRoleMenu::getMenuId).toList();
    }

    private void insertRoleMenu(Long roleId, List<Long> menuIds) {
        if (menuIds != null) {
            for (Long menuId : menuIds) {
                roleMenuMapper.insert(new SysRoleMenu(roleId, menuId));
            }
        }
    }

    private void insertRoleDept(Long roleId, List<Long> deptIds) {
        if (deptIds != null) {
            for (Long deptId : deptIds) {
                roleDeptMapper.insert(new SysRoleDept(roleId, deptId));
            }
        }
    }
}
