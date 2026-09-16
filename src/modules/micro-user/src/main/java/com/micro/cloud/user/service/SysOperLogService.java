package com.micro.cloud.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.micro.cloud.user.domain.SysOperLog;
import com.micro.cloud.user.mapper.SysOperLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 操作日志服务
 */
@Service
@RequiredArgsConstructor
public class SysOperLogService extends ServiceImpl<SysOperLogMapper, SysOperLog> {

    /** 操作日志列表 */
    public List<SysOperLog> selectOperLogList(SysOperLog operLog) {
        LambdaQueryWrapper<SysOperLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(operLog.getTitle() != null, SysOperLog::getTitle, operLog.getTitle())
                .eq(operLog.getBusinessType() != null, SysOperLog::getBusinessType, operLog.getBusinessType())
                .eq(operLog.getStatus() != null, SysOperLog::getStatus, operLog.getStatus())
                .like(operLog.getOperName() != null, SysOperLog::getOperName, operLog.getOperName())
                .orderByDesc(SysOperLog::getOperTime);
        return list(wrapper);
    }

    /** 清空操作日志 */
    public void cleanOperLog() {
        remove(new LambdaQueryWrapper<>());
    }
}
