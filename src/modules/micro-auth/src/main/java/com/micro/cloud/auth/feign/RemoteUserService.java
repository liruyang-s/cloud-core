package com.micro.cloud.auth.feign;

import com.micro.cloud.common.core.domain.Result;
import com.micro.cloud.common.mybatis.core.LoginUser;
import com.micro.cloud.common.web.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 用户服务远程调用接口
 */
@FeignClient(value = "micro-user", configuration = FeignConfig.class)
public interface RemoteUserService {

    /**
     * 根据用户名查询登录用户信息（含角色、权限、数据范围）
     */
    @GetMapping("/inner/user/info/{username}")
    Result<LoginUser> getUserInfo(@PathVariable("username") String username);
}
