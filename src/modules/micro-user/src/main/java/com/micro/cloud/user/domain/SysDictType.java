package com.micro.cloud.user.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典类型表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dict_type")
public class SysDictType extends BaseEntity {

    @TableId(value = "dict_id", type = IdType.AUTO)
    private Long dictId;

    /** 字典名称 */
    @NotBlank(message = "字典名称不能为空")
    private String dictName;

    /** 字典类型 */
    @NotBlank(message = "字典类型不能为空")
    private String dictType;

    /** 状态（0正常 1停用） */
    private String status;
}
