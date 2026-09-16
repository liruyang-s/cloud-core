package com.micro.cloud.user.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 角色和部门关联表（自定义部门数据权限依赖）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_role_dept")
public class SysRoleDept implements Serializable {

    /** 角色ID */
    private Long roleId;

    /** 部门ID */
    private Long deptId;
}
