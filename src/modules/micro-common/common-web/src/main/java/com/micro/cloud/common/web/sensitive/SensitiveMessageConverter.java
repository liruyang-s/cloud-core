package com.micro.cloud.common.web.sensitive;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

import java.util.regex.Pattern;

/**
 * 日志脱敏转换器（logback）
 * <p>
 * 对日志消息中的手机号、身份证号做正则兜底脱敏，防止敏感信息打印到日志。
 * <p>
 * 在 logback 配置中启用：
 * <pre>
 * &lt;conversionRule conversionWord="desensitizedMsg"
 *     converterClass="com.micro.cloud.common.web.sensitive.SensitiveMessageConverter"/&gt;
 * &lt;pattern&gt;%d [%thread] [%X{traceId}] %-5level %logger - %desensitizedMsg%n&lt;/pattern&gt;
 * </pre>
 */
public class SensitiveMessageConverter extends MessageConverter {

    /** 手机号（11 位，1 开头） */
    private static final Pattern MOBILE_PATTERN =
            Pattern.compile("(?<!\\d)(1[3-9]\\d)\\d{4}(\\d{4})(?!\\d)");

    /** 身份证号（18 位） */
    private static final Pattern ID_CARD_PATTERN =
            Pattern.compile("(?<!\\d)(\\d{3})\\d{11}(\\d{4})(?!\\d)");

    @Override
    public String convert(ILoggingEvent event) {
        String message = super.convert(event);
        if (message == null) {
            return null;
        }
        message = MOBILE_PATTERN.matcher(message).replaceAll("$1****$2");
        message = ID_CARD_PATTERN.matcher(message).replaceAll("$1***********$2");
        return message;
    }
}
