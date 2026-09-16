package com.micro.cloud.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.micro.cloud.user.domain.SysLoginLog;
import com.micro.cloud.user.mapper.SysLoginLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 登录日志服务
 */
@Service
@RequiredArgsConstructor
public class SysLoginLogService extends ServiceImpl<SysLoginLogMapper, SysLoginLog> {

    /** 登录日志列表 */
    public List<SysLoginLog> selectLoginLogList(SysLoginLog loginLog) {
        LambdaQueryWrapper<SysLoginLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(loginLog.getUserName() != null, SysLoginLog::getUserName, loginLog.getUserName())
                .eq(loginLog.getStatus() != null, SysLoginLog::getStatus, loginLog.getStatus())
                .orderByDesc(SysLoginLog::getLoginTime);
        return list(wrapper);
    }

    /** 清空登录日志 */
    public void cleanLoginLog() {
        remove(new LambdaQueryWrapper<>());
    }
}
