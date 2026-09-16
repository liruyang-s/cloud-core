package com.micro.cloud.user.config;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import com.micro.cloud.common.mybatis.core.LoginUser;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Sa-Token 权限数据源实现
 * <p>
 * 从登录会话中读取 {@link LoginUser}，返回其权限标识与角色标识，
 * 供 {@code @SaCheckPermission} / {@code @SaCheckRole} 注解校验使用。
 */
@Component
public class StpInterfaceImpl implements StpInterface {

    /**
     * 返回指定账号拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        LoginUser loginUser = getLoginUser(loginId);
        if (loginUser == null) {
            return Collections.emptyList();
        }
        // 超级管理员拥有全部权限
        if (loginUser.isAdmin()) {
            return Collections.singletonList("*");
        }
        if (loginUser.getPermissions() == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(loginUser.getPermissions());
    }

    /**
     * 返回指定账号拥有的角色标识集合
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        LoginUser loginUser = getLoginUser(loginId);
        if (loginUser == null || loginUser.getRoleKeys() == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(loginUser.getRoleKeys());
    }

    private LoginUser getLoginUser(Object loginId) {
        try {
            SaSession session = StpUtil.getSessionByLoginId(loginId, false);
            if (session == null) {
                return null;
            }
            Object obj = session.get("loginUser");
            return obj instanceof LoginUser user ? user : null;
        } catch (Exception e) {
            return null;
        }
    }
}
