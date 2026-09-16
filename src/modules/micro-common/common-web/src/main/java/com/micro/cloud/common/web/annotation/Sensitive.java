package com.micro.cloud.common.web.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.micro.cloud.common.web.sensitive.SensitiveSerializer;
import com.micro.cloud.common.web.sensitive.SensitiveType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 敏感字段脱敏注解
 * <p>
 * 标注在实体字段上，接口返回时按 {@link SensitiveType} 规则自动脱敏。
 * <pre>
 * &#64;Sensitive(SensitiveType.MOBILE)
 * private String phone;
 * </pre>
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@JacksonAnnotationsInside
@JsonSerialize(using = SensitiveSerializer.class)
public @interface Sensitive {

    /** 脱敏类型 */
    SensitiveType value();
}
