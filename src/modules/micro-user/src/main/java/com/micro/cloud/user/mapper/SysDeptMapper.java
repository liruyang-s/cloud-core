package com.micro.cloud.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.micro.cloud.user.domain.SysDept;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

/**
 * 部门 Mapper
 */
@Mapper
public interface SysDeptMapper extends BaseMapper<SysDept> {

    /**
     * 查询自定义数据权限可访问的部门ID集合
     */
    Set<Long> selectDeptIdsByRoleId(@Param("roleId") Long roleId);
}
