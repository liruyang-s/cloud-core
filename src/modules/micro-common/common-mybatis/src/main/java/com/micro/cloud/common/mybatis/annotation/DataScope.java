package com.micro.cloud.common.mybatis.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据权限注解
 * <p>
 * 标注在 Mapper 方法上，由数据权限拦截器根据当前登录用户的角色数据范围，
 * 动态拼接 SQL 过滤条件，实现行级数据权限控制。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScope {

    /** 部门表别名 */
    String deptAlias() default "d";

    /** 用户表别名 */
    String userAlias() default "u";
}
