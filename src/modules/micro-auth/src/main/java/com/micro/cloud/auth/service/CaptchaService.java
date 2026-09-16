package com.micro.cloud.auth.service;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * 图形验证码服务
 * <p>
 * 验证码答案存入 Redis（2 分钟过期），校验后立即删除，防止重复使用。
 */
@Service
@RequiredArgsConstructor
public class CaptchaService {

    private final StringRedisTemplate stringRedisTemplate;

    /** 验证码键前缀 */
    private static final String CAPTCHA_KEY = "captcha:";
    /** 验证码有效期 */
    private static final Duration EXPIRE = Duration.ofMinutes(2);

    /**
     * 生成验证码
     *
     * @return uuid（唯一标识）与 img（Base64 图片）
     */
    public Map<String, String> generate() {
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(120, 40, 4, 30);
        String uuid = IdUtil.fastSimpleUUID();
        stringRedisTemplate.opsForValue().set(CAPTCHA_KEY + uuid, captcha.getCode().toLowerCase(), EXPIRE);
        Map<String, String> result = new HashMap<>(4);
        result.put("uuid", uuid);
        result.put("img", captcha.getImageBase64Data());
        return result;
    }

    /**
     * 校验验证码（一次性，校验后立即删除，防止重放）
     */
    public boolean verify(String uuid, String code) {
        if (StrUtil.isBlank(uuid) || StrUtil.isBlank(code)) {
            return false;
        }
        String key = CAPTCHA_KEY + uuid;
        String realCode = stringRedisTemplate.opsForValue().get(key);
        stringRedisTemplate.delete(key);
        return realCode != null && realCode.equalsIgnoreCase(code.trim());
    }
}
