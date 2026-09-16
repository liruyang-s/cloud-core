package com.micro.cloud.user.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 参数配置表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_config")
public class SysConfig extends BaseEntity {

    @TableId(value = "config_id", type = IdType.AUTO)
    private Long configId;

    /** 参数名称 */
    @NotBlank(message = "参数名称不能为空")
    private String configName;

    /** 参数键名 */
    @NotBlank(message = "参数键名不能为空")
    private String configKey;

    /** 参数键值 */
    private String configValue;

    /** 系统内置（Y是 N否） */
    private String configType;
}
