package com.micro.cloud.user.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 角色和菜单关联表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_role_menu")
public class SysRoleMenu implements Serializable {

    /** 角色ID */
    private Long roleId;

    /** 菜单ID */
    private Long menuId;
}
