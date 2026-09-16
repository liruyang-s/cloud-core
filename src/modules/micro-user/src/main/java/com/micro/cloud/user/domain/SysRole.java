package com.micro.cloud.user.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 角色信息表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class SysRole extends BaseEntity {

    @TableId(value = "role_id", type = IdType.AUTO)
    private Long roleId;

    /** 角色名称 */
    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    /** 角色权限字符串 */
    @NotBlank(message = "权限字符不能为空")
    private String roleKey;

    /** 显示顺序 */
    @NotNull(message = "显示顺序不能为空")
    private Integer roleSort;

    /** 数据范围（1全部 2自定义 3本部门 4本部门及以下 5仅本人） */
    private String dataScope;

    /** 角色状态（0正常 1停用） */
    private String status;

    /** 删除标志 */
    @TableLogic
    private String delFlag;

    /** 菜单ID列表（非表字段） */
    @TableField(exist = false)
    private List<Long> menuIds;

    /** 部门ID列表（自定义数据权限，非表字段） */
    @TableField(exist = false)
    private List<Long> deptIds;
}
