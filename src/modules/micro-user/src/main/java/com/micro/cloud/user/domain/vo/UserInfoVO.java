package com.micro.cloud.user.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 当前登录用户信息视图对象（不含密码等敏感字段）
 */
@Data
public class UserInfoVO implements Serializable {

    /** 用户ID */
    private Long userId;

    /** 用户账号 */
    private String userName;

    /** 昵称 */
    private String nickName;

    /** 头像地址 */
    private String avatar;

    /** 部门ID */
    private Long deptId;

    /** 角色标识集合 */
    private List<String> roles;

    /** 权限标识集合 */
    private List<String> permissions;
}
