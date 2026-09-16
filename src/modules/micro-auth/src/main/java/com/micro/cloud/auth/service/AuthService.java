package com.micro.cloud.auth.service;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.micro.cloud.auth.domain.LoginBody;
import com.micro.cloud.auth.feign.RemoteUserService;
import com.micro.cloud.common.core.constant.Constants;
import com.micro.cloud.common.core.domain.Result;
import com.micro.cloud.common.core.exception.ServiceException;
import com.micro.cloud.common.mybatis.core.LoginUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * 认证服务：登录、登出全流程
 * <p>
 * 会话采用 Sa-Token Redis 集中式存储模式，支持分布式会话共享与单点登录；
 * 密码采用 BCrypt 加密存储，内置连续失败锁定策略。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final RemoteUserService remoteUserService;
    private final StringRedisTemplate stringRedisTemplate;

    /** 登录失败次数键前缀 */
    private static final String LOGIN_FAIL_KEY = "login:fail:";
    /** 最大失败次数 */
    private static final int MAX_FAIL_COUNT = 5;
    /** 锁定时长 */
    private static final Duration LOCK_DURATION = Duration.ofMinutes(10);

    /**
     * 账号密码登录
     */
    public SaTokenInfo login(LoginBody body) {
        String username = body.getUsername();
        // 连续失败锁定校验
        String failKey = LOGIN_FAIL_KEY + username;
        String failCountStr = stringRedisTemplate.opsForValue().get(failKey);
        int failCount = failCountStr == null ? 0 : Integer.parseInt(failCountStr);
        if (failCount >= MAX_FAIL_COUNT) {
            throw new ServiceException("密码连续错误 " + MAX_FAIL_COUNT + " 次，账户已锁定 " + LOCK_DURATION.toMinutes() + " 分钟");
        }

        // Feign 远程调用用户服务获取用户信息
        Result<LoginUser> userResult = remoteUserService.getUserInfo(username);
        if (userResult == null || userResult.getCode() != Constants.SUCCESS || userResult.getData() == null) {
            throw new ServiceException("用户不存在");
        }
        LoginUser loginUser = userResult.getData();
        if (Constants.STATUS_DISABLE.equals(loginUser.getStatus())) {
            throw new ServiceException("账户已被停用，请联系管理员");
        }

        // BCrypt 密码校验
        if (!BCrypt.checkpw(body.getPassword(), loginUser.getPassword())) {
            stringRedisTemplate.opsForValue().increment(failKey);
            stringRedisTemplate.expire(failKey, LOCK_DURATION);
            throw new ServiceException("用户名或密码错误，剩余尝试次数：" + (MAX_FAIL_COUNT - failCount - 1));
        }

        // 登录成功：清除失败计数，签发 Token（会话写入 Redis 集中存储）
        stringRedisTemplate.delete(failKey);
        StpUtil.login(loginUser.getUserId());
        // 将用户上下文写入会话，供业务服务解析
        StpUtil.getSession().set("loginUser", loginUser);
        log.info("用户登录成功: {}", username);
        return StpUtil.getTokenInfo();
    }

    /**
     * 注销登录
     */
    public void logout() {
        StpUtil.logout();
    }
}
