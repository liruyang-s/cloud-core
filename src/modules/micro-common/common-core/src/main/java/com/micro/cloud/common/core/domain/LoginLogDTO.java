package com.micro.cloud.common.core.domain;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 登录日志传输对象（认证中心 -> 用户服务）
 */
@Data
public class LoginLogDTO implements Serializable {

    /** 用户账号 */
    private String userName;

    /** 登录IP地址 */
    private String ipaddr;

    /** 登录状态（0成功 1失败） */
    private String status;

    /** 提示信息 */
    private String msg;

    /** 访问时间 */
    private LocalDateTime loginTime;

    /** 链路追踪ID */
    private String traceId;
}
