package com.micro.cloud.user.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典数据表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dict_data")
public class SysDictData extends BaseEntity {

    @TableId(value = "dict_code", type = IdType.AUTO)
    private Long dictCode;

    /** 字典排序 */
    private Integer dictSort;

    /** 字典标签 */
    @NotBlank(message = "字典标签不能为空")
    private String dictLabel;

    /** 字典键值 */
    @NotBlank(message = "字典键值不能为空")
    private String dictValue;

    /** 字典类型 */
    @NotBlank(message = "字典类型不能为空")
    private String dictType;

    /** 样式属性 */
    private String cssClass;

    /** 表格回显样式 */
    private String listClass;

    /** 是否默认（Y是 N否） */
    private String isDefault;

    /** 状态（0正常 1停用） */
    private String status;
}
