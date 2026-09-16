package com.micro.cloud.user.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 实体基类：公共审计字段
 */
@Data
public class BaseEntity implements Serializable {

    /** 创建者 */
    @TableField(value = "create_by")
    private String createBy;

    /** 创建时间 */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新者 */
    @TableField(value = "update_by")
    private String updateBy;

    /** 更新时间 */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 备注 */
    @TableField(value = "remark")
    private String remark;
}
