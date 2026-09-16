package com.micro.cloud.user.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.same.SaSameUtil;
import cn.dev33.satoken.stp.StpUtil;
import com.micro.cloud.user.interceptor.LoginUserInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 * <p>
 * SaInterceptor：服务侧二次鉴权（网关已做第一道拦截），
 * 同时启用 {@code @SaCheckPermission} 等注解校验。
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final LoginUserInterceptor loginUserInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Sa-Token 鉴权拦截器（含注解鉴权）
        registry.addInterceptor(new SaInterceptor(handle -> {
                    // 内部 RPC 接口：仅校验 Same-Token，防止外部伪造内部调用
                    SaRouter.match("/inner/**")
                            .check(r -> SaSameUtil.checkToken(
                                    SaHolder.getRequest().getHeader(SaSameUtil.SAME_TOKEN)));
                    // 其余接口：校验登录状态
                    SaRouter.match("/**")
                            .notMatch("/inner/**")
                            .check(r -> StpUtil.checkLogin());
                }))
                .addPathPatterns("/**");
        // 登录上下文拦截器（填充 LoginUserHolder）
        registry.addInterceptor(loginUserInterceptor).addPathPatterns("/**");
    }
}
