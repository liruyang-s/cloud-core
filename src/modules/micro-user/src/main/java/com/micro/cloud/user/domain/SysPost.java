package com.micro.cloud.user.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 岗位信息表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_post")
public class SysPost extends BaseEntity {

    @TableId(value = "post_id", type = IdType.AUTO)
    private Long postId;

    /** 岗位编码 */
    @NotBlank(message = "岗位编码不能为空")
    private String postCode;

    /** 岗位名称 */
    @NotBlank(message = "岗位名称不能为空")
    private String postName;

    /** 显示顺序 */
    @NotNull(message = "显示顺序不能为空")
    private Integer postSort;

    /** 状态（0正常 1停用） */
    private String status;
}
