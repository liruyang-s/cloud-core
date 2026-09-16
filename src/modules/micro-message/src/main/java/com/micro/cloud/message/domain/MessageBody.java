package com.micro.cloud.message.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * 站内消息体
 */
@Data
public class MessageBody implements Serializable {

    /** 消息唯一ID（幂等键） */
    private String msgId;

    /** 接收用户ID */
    private Long userId;

    /** 消息标题 */
    private String title;

    /** 消息内容 */
    private String content;

    /** 消息类型（1通知 2公告 3提醒） */
    private String type;
}
