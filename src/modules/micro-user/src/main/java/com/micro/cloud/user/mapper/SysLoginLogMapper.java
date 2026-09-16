package com.micro.cloud.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.micro.cloud.user.domain.SysLoginLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 登录日志 Mapper
 */
@Mapper
public interface SysLoginLogMapper extends BaseMapper<SysLoginLog> {
}
