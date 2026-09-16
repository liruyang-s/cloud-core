package com.micro.cloud.user.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 通知公告表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_notice")
public class SysNotice extends BaseEntity {

    @TableId(value = "notice_id", type = IdType.AUTO)
    private Long noticeId;

    /** 公告标题 */
    @NotBlank(message = "公告标题不能为空")
    private String noticeTitle;

    /** 公告类型（1通知 2公告） */
    @NotBlank(message = "公告类型不能为空")
    private String noticeType;

    /** 公告内容 */
    private String noticeContent;

    /** 公告状态（0正常 1关闭） */
    private String status;
}
