package com.micro.cloud.common.core.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码工具（BCrypt）
 */
public final class SecurityUtils {

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    private SecurityUtils() {
    }

    /**
     * 加密密码
     */
    public static String encryptPassword(String rawPassword) {
        return ENCODER.encode(rawPassword);
    }

    /**
     * 校验密码
     */
    public static boolean matchesPassword(String rawPassword, String encodedPassword) {
        return ENCODER.matches(rawPassword, encodedPassword);
    }
}
