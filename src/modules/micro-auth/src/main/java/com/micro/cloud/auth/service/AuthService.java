package com.micro.cloud.auth.service;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.micro.cloud.auth.domain.LoginBody;
import com.micro.cloud.auth.feign.RemoteUserService;
import com.micro.cloud.common.core.constant.Constants;
import com.micro.cloud.common.core.domain.LoginLogDTO;
import com.micro.cloud.common.core.domain.Result;
import com.micro.cloud.common.core.exception.ServiceException;
import com.micro.cloud.common.mybatis.core.LoginUser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 认证服务：登录、登出全流程
 * <p>
 * 会话采用 Sa-Token Redis 集中式存储模式，支持分布式会话共享与单点登录；
 * 密码采用 BCrypt 加密存储，内置连续失败锁定策略（Redis 原子计数）、
 * 图形验证码校验与登录日志记录。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final RemoteUserService remoteUserService;
    private final StringRedisTemplate stringRedisTemplate;
    private final CaptchaService captchaService;

    /** 登录失败次数键前缀 */
    private static final String LOGIN_FAIL_KEY = "login:fail:";
    /** 最大失败次数 */
    private static final int MAX_FAIL_COUNT = 5;
    /** 锁定时长 */
    private static final Duration LOCK_DURATION = Duration.ofMinutes(10);
    /** 统一报错文案（避免泄露账号是否存在） */
    private static final String LOGIN_ERROR_MSG = "用户名或密码错误";

    /** 验证码开关（生产环境建议开启） */
    @Value("${security.captcha-enabled:true}")
    private boolean captchaEnabled;

    /**
     * 账号密码登录
     */
    public SaTokenInfo login(LoginBody body, HttpServletRequest request) {
        String username = body.getUsername();

        // 验证码校验（一次性，防止重放）
        if (captchaEnabled && !captchaService.verify(body.getUuid(), body.getCode())) {
            throw new ServiceException("验证码错误或已过期");
        }

        // 连续失败锁定校验
        String failKey = LOGIN_FAIL_KEY + username;
        String failCountStr = stringRedisTemplate.opsForValue().get(failKey);
        int failCount = failCountStr == null ? 0 : Integer.parseInt(failCountStr);
        if (failCount >= MAX_FAIL_COUNT) {
            throw new ServiceException("密码连续错误 " + MAX_FAIL_COUNT + " 次，账户已锁定 " + LOCK_DURATION.toMinutes() + " 分钟");
        }

        // Feign 远程调用用户服务获取用户信息
        Result<LoginUser> userResult = remoteUserService.getUserInfo(username);
        LoginUser loginUser = (userResult == null || userResult.getCode() != Constants.SUCCESS) ? null : userResult.getData();

        // 账号不存在或密码错误统一报错，避免泄露账号是否存在
        if (loginUser == null || !BCrypt.checkpw(body.getPassword(), loginUser.getPassword())) {
            recordLoginFail(username, request, LOGIN_ERROR_MSG);
            long count = recordFailCount(failKey);
            long remain = MAX_FAIL_COUNT - count;
            throw new ServiceException(remain > 0
                    ? LOGIN_ERROR_MSG + "，剩余尝试次数：" + remain
                    : LOGIN_ERROR_MSG + "，账户已锁定 " + LOCK_DURATION.toMinutes() + " 分钟");
        }

        if (Constants.STATUS_DISABLE.equals(loginUser.getStatus())) {
            recordLoginFail(username, request, "账户已停用");
            throw new ServiceException("账户已被停用，请联系管理员");
        }

        // 登录成功：清除失败计数，签发 Token（会话写入 Redis 集中存储）
        stringRedisTemplate.delete(failKey);
        StpUtil.login(loginUser.getUserId());
        // 将用户上下文写入会话，供业务服务解析
        StpUtil.getSession().set("loginUser", loginUser);
        recordLoginSuccess(username, request);
        log.info("用户登录成功: {}", username);
        return StpUtil.getTokenInfo();
    }

    /**
     * 注销登录
     */
    public void logout() {
        StpUtil.logout();
    }

    /**
     * 失败计数原子自增：仅首次失败设置过期时间，
     * 避免攻击者通过持续尝试不断刷新锁定窗口
     */
    private long recordFailCount(String failKey) {
        Long count = stringRedisTemplate.opsForValue().increment(failKey);
        long current = count == null ? 1 : count;
        if (current == 1) {
            stringRedisTemplate.expire(failKey, LOCK_DURATION);
        }
        return current;
    }

    private void recordLoginSuccess(String username, HttpServletRequest request) {
        recordLoginLog(username, Constants.LOGIN_SUCCESS, "登录成功", request);
    }

    private void recordLoginFail(String username, HttpServletRequest request, String msg) {
        recordLoginLog(username, Constants.LOGIN_FAIL, msg, request);
    }

    /**
     * 异步记录登录日志（失败不影响登录主流程）
     */
    private void recordLoginLog(String username, String status, String msg, HttpServletRequest request) {
        try {
            LoginLogDTO loginLog = new LoginLogDTO();
            loginLog.setUserName(username);
            loginLog.setStatus(status);
            loginLog.setMsg(msg);
            loginLog.setIpaddr(getClientIp(request));
            loginLog.setLoginTime(LocalDateTime.now());
            loginLog.setTraceId(MDC.get(Constants.TRACE_ID_KEY));
            remoteUserService.recordLoginLog(loginLog);
        } catch (Exception e) {
            log.warn("记录登录日志失败: {}", e.getMessage());
        }
    }

    /**
     * 获取客户端真实 IP（兼容网关/Nginx 代理）
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            // 多级代理时取第一个非 unknown 的 IP
            int index = ip.indexOf(',');
            return index > 0 ? ip.substring(0, index).trim() : ip.trim();
        }
        ip = request.getHeader("X-Real-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }
}
