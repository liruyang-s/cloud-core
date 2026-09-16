package com.micro.cloud.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.micro.cloud.user.domain.SysOperLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志 Mapper
 */
@Mapper
public interface SysOperLogMapper extends BaseMapper<SysOperLog> {
}
