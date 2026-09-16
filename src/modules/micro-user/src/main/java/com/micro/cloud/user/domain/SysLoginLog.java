package com.micro.cloud.user.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统访问记录（登录日志）
 */
@Data
@TableName("sys_login_log")
public class SysLoginLog implements Serializable {

    @TableId(value = "info_id", type = IdType.AUTO)
    private Long infoId;

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
