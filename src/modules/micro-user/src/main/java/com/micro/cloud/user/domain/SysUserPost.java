package com.micro.cloud.user.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户与岗位关联表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_user_post")
public class SysUserPost implements Serializable {

    /** 用户ID */
    private Long userId;

    /** 岗位ID */
    private Long postId;
}
