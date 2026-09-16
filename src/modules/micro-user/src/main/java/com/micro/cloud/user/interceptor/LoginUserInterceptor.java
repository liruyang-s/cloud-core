package com.micro.cloud.user.interceptor;

import cn.dev33.satoken.stp.StpUtil;
import com.micro.cloud.common.mybatis.core.LoginUser;
import com.micro.cloud.common.mybatis.core.LoginUserHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登录上下文拦截器
 * <p>
 * 从 Sa-Token 会话解析登录用户信息，填充到 LoginUserHolder，
 * 供数据权限拦截器等组件使用；请求结束后清理 ThreadLocal。
 */
@Component
public class LoginUserInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (StpUtil.isLogin()) {
            Object loginUser = StpUtil.getSession().get("loginUser");
            if (loginUser instanceof LoginUser user) {
                LoginUserHolder.set(user);
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        LoginUserHolder.clear();
    }
}
