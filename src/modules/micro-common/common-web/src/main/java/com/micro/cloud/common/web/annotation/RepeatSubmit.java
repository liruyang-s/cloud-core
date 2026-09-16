package com.micro.cloud.common.web.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 防重复提交注解
 * <p>
 * 标注在 Controller 方法上，基于 Redis SETNX 在指定时间窗口内拦截重复请求。
 * key 由「请求路径 + 用户标识（Sa-Token loginId，未登录则取客户端 IP）」组成。
 * <p>
 * 使用示例：
 * <pre>
 * &#64;RepeatSubmit(interval = 3000, message = "请勿重复提交")
 * &#64;PostMapping("/save")
 * public Result&lt;Void&gt; save(...) { ... }
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RepeatSubmit {

    /** 防重时间窗口（毫秒），默认 3 秒 */
    long interval() default 3000;

    /** 重复提交时的提示信息 */
    String message() default "请勿重复提交";
}
