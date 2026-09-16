package com.micro.cloud.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.micro.cloud.common.core.exception.ServiceException;
import com.micro.cloud.user.domain.SysDept;
import com.micro.cloud.user.mapper.SysDeptMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 部门服务
 */
@Service
@RequiredArgsConstructor
public class SysDeptService extends ServiceImpl<SysDeptMapper, SysDept> {

    /** 部门列表 */
    public List<SysDept> selectDeptList(SysDept dept) {
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(dept.getDeptName() != null, SysDept::getDeptName, dept.getDeptName())
                .eq(dept.getStatus() != null, SysDept::getStatus, dept.getStatus())
                .orderByAsc(SysDept::getParentId, SysDept::getOrderNum);
        return list(wrapper);
    }

    /** 构建部门树 */
    public List<SysDept> buildDeptTree(List<SysDept> depts) {
        List<SysDept> roots = new ArrayList<>();
        for (SysDept dept : depts) {
            if (dept.getParentId() == null || dept.getParentId() == 0) {
                roots.add(dept);
            } else {
                depts.stream()
                        .filter(d -> d.getDeptId().equals(dept.getParentId()))
                        .findFirst()
                        .ifPresentOrElse(parent -> parent.getChildren().add(dept),
                                () -> roots.add(dept));
            }
        }
        return roots;
    }

    /** 新增部门（维护祖级列表） */
    public void insertDept(SysDept dept) {
        SysDept parent = getById(dept.getParentId());
        if (parent == null) {
            throw new ServiceException("上级部门不存在");
        }
        dept.setAncestors(parent.getAncestors() + "," + dept.getParentId());
        save(dept);
    }

    /** 修改部门 */
    public void updateDept(SysDept dept) {
        SysDept parent = getById(dept.getParentId());
        if (parent != null) {
            dept.setAncestors(parent.getAncestors() + "," + dept.getParentId());
        }
        updateById(dept);
    }
}
