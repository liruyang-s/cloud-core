package com.micro.cloud.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.micro.cloud.common.mybatis.annotation.DataScope;
import com.micro.cloud.user.domain.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户信息 Mapper
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 查询用户列表（数据权限过滤）
     */
    @DataScope(deptAlias = "d", userAlias = "u")
    List<SysUser> selectUserList(@Param("userName") String userName,
                                 @Param("phonenumber") String phonenumber,
                                 @Param("status") String status,
                                 @Param("deptId") Long deptId);

    /**
     * 校验用户名是否唯一
     */
    SysUser selectByUserName(@Param("userName") String userName);
}
