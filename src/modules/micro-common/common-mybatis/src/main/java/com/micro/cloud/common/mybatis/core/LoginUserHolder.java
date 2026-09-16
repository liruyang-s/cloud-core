package com.micro.cloud.common.mybatis.core;

/**
 * 登录用户上下文持有者
 * <p>
 * 由业务服务在请求进入时（如拦截器中解析 Sa-Token 会话）填充，
 * 数据权限拦截器从此处获取当前用户的数据范围。
 */
public final class LoginUserHolder {

    private static final ThreadLocal<LoginUser> HOLDER = new ThreadLocal<>();

    private LoginUserHolder() {
    }

    public static void set(LoginUser loginUser) {
        HOLDER.set(loginUser);
    }

    public static LoginUser get() {
        return HOLDER.get();
    }

    public static void clear() {
        HOLDER.remove();
    }
}
