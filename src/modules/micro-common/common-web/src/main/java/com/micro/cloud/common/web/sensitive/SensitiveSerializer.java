package com.micro.cloud.common.web.sensitive;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.micro.cloud.common.web.annotation.Sensitive;

import java.io.IOException;

/**
 * 敏感字段序列化器
 * <p>
 * 配合 {@link Sensitive} 注解，在 JSON 序列化时按类型规则脱敏。
 */
public class SensitiveSerializer extends JsonSerializer<String> implements ContextualSerializer {

    private SensitiveType type = SensitiveType.PASSWORD;

    public SensitiveSerializer() {
    }

    public SensitiveSerializer(SensitiveType type) {
        this.type = type;
    }

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(desensitize(value, type));
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property)
            throws JsonMappingException {
        if (property != null) {
            Sensitive annotation = property.getAnnotation(Sensitive.class);
            if (annotation != null) {
                return new SensitiveSerializer(annotation.value());
            }
        }
        return this;
    }

    /**
     * 按类型执行脱敏
     */
    public static String desensitize(String value, SensitiveType type) {
        if (StrUtil.isBlank(value)) {
            return value;
        }
        return switch (type) {
            case MOBILE -> StrUtil.hide(value, 3, 7);
            case ID_CARD -> StrUtil.hide(value, 3, value.length() - 4);
            case CHINESE_NAME -> value.length() <= 1 ? value
                    : StrUtil.hide(value, 1, value.length() - 1);
            case EMAIL -> desensitizeEmail(value);
            case BANK_CARD -> StrUtil.hide(value, 4, value.length() - 4);
            case ADDRESS -> value.length() <= 6 ? "******"
                    : value.substring(0, 6) + "****";
            case PASSWORD -> "******";
        };
    }

    private static String desensitizeEmail(String value) {
        int atIndex = value.indexOf('@');
        if (atIndex <= 1) {
            return value;
        }
        return value.charAt(0) + "***" + value.substring(atIndex);
    }
}
