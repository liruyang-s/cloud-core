package com.micro.cloud.common.mybatis.core;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * 当前登录用户上下文（由认证信息填充，供数据权限等组件使用）
 */
@Data
public class LoginUser implements Serializable {

    /** 用户ID */
    private Long userId;

    /** 用户账号 */
    private String userName;

    /** 昵称 */
    private String nickName;

    /** 部门ID */
    private Long deptId;

    /** 密码（BCrypt，仅认证中心内部接口返回） */
    private String password;

    /** 帐号状态（0正常 1停用） */
    private String status;

    /** 菜单权限标识集合 */
    private Set<String> permissions;

    /** 角色权限标识集合 */
    private Set<String> roleKeys;

    /** 数据范围（取所有角色中最大范围：1全部 2自定义 3本部门 4本部门及以下 5仅本人） */
    private String dataScope;

    /** 自定义数据权限时可访问的部门ID集合 */
    private Set<Long> dataScopeDeptIds;

    /** 是否超级管理员 */
    private boolean admin;
}
