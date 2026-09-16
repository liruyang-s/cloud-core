package com.micro.cloud.gateway.config;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.SaTokenException;
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

    /** 白名单路径：登录、验证码、接口文档（生产环境建议通过 knife4j.production=true 关闭文档） */
    private static final String[] WHITE_LIST = {
            "/auth/login",
            "/auth/captcha",
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
                .setAuth(obj -> {
                    // 内部 RPC 接口（/inner/**）仅供服务间调用，经网关的外部访问一律拒绝
                    SaRouter.match("/inner/**", "/**/inner/**")
                            .check(r -> {
                                throw new SaTokenException("内部接口，禁止外部访问");
                            });
                    SaRouter.match("/**")
                            .notMatch(WHITE_LIST)
                            .check(r -> StpUtil.checkLogin());
                })
                .setError(e -> {
                    if (e instanceof NotLoginException) {
                        return SaResult.error("未登录或 Token 已过期").setCode(401);
                    }
                    return SaResult.error(e.getMessage()).setCode(403);
                });
    }
}
