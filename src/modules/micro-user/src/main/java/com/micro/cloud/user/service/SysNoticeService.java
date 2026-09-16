package com.micro.cloud.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.micro.cloud.user.domain.SysNotice;
import com.micro.cloud.user.mapper.SysNoticeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 通知公告服务
 */
@Service
@RequiredArgsConstructor
public class SysNoticeService extends ServiceImpl<SysNoticeMapper, SysNotice> {

    /** 公告列表 */
    public List<SysNotice> selectNoticeList(SysNotice notice) {
        LambdaQueryWrapper<SysNotice> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(notice.getNoticeTitle() != null, SysNotice::getNoticeTitle, notice.getNoticeTitle())
                .eq(notice.getNoticeType() != null, SysNotice::getNoticeType, notice.getNoticeType())
                .eq(notice.getStatus() != null, SysNotice::getStatus, notice.getStatus())
                .orderByDesc(SysNotice::getCreateTime);
        return list(wrapper);
    }
}
