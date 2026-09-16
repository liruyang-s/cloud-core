package com.micro.cloud.common.core.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * 分页查询参数
 */
@Data
public class PageQuery implements Serializable {

    /** 当前页码，默认 1 */
    private Integer pageNum = 1;

    /** 每页条数，默认 10 */
    private Integer pageSize = 10;
}
