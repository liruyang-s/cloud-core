package com.micro.cloud.auth.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 登录请求体
 */
@Data
public class LoginBody implements Serializable {

    /** 用户账号 */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /** 密码 */
    @NotBlank(message = "密码不能为空")
    private String password;
}
