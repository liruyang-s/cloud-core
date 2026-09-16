package com.micro.cloud.common.web.sensitive;

/**
 * 敏感字段脱敏类型
 */
public enum SensitiveType {

    /** 手机号：138****8000 */
    MOBILE,

    /** 身份证：110***********1234 */
    ID_CARD,

    /** 姓名：张*三 */
    CHINESE_NAME,

    /** 邮箱：z***@example.com */
    EMAIL,

    /** 银行卡：6222 **** **** 1234 */
    BANK_CARD,

    /** 地址：北京市朝阳区**** */
    ADDRESS,

    /** 密码：全部替换为 ****** */
    PASSWORD
}
