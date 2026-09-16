package com.micro.cloud.gateway.config;

import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.reactor.spring.SaTokenContextRegister;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Sa-Token 网关鉴权配置（响应式）
 * <p>
 * 在网关层统一校验 Token 合法性，非法请求直接拦截，不穿透到业务服务。
 * <p>
 * 注意：sa-token-reactor-spring-boot-starter 未提供自动装配，
 * 必须手动导入 {@link SaTokenContextRegister} 注册响应式上下文，
 * 否则 StpUtil 调用会抛出"未能获取有效的上下文处理器"。
 */
@Configuration
@Import(SaTokenContextRegister.class)
public class SaTokenConfig {

    /** 白名单路径：登录、验证码、健康检查、接口文档 */
    private static final String[] WHITE_LIST = {
            "/auth/login",
            "/auth/captcha",
            "/actuator/**",
            // 经网关访问各服务的健康检查（路径带服务前缀）
            "/*/actuator/**",
            "/doc.html",
            "/webjars/**",
            "/v3/api-docs/**",
            "/*/v3/api-docs/**"
    };

    @Bean
    public SaReactorFilter saReactorFilter() {
        return new SaReactorFilter()
                .addInclude("/**")
                .addExclude("/favicon.ico")
                .setAuth(obj ->
                        SaRouter.match("/**")
                                .notMatch(WHITE_LIST)
                                .check(r -> StpUtil.checkLogin())
                )
                .setError(e -> SaResult.error(e.getMessage()).setCode(401));
    }
}
