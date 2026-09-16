package com.micro.cloud.user.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户和角色关联表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_user_role")
public class SysUserRole implements Serializable {

    /** 用户ID */
    private Long userId;

    /** 角色ID */
    private Long roleId;
}
