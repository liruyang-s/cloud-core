package com.micro.cloud.demo.service;

import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Seata 分布式事务示例服务
 * <p>
 * 底座仅提供框架集成与用法演示，实际业务事务逻辑由业务服务自行编写。
 * <p>
 * 典型用法：在跨服务操作的入口方法标注 {@link GlobalTransactional}，
 * 方法内依次执行本地数据库操作与 Feign 远程调用，任一环节失败整体回滚。
 */
@Slf4j
@Service
public class SeataDemoService {

    /**
     * 跨服务事务示例骨架
     * <p>
     * rollbackFor 指定触发回滚的异常类型；
     * name 用于 Seata 控制台定位事务。
     */
    @GlobalTransactional(name = "demo-global-tx", rollbackFor = Exception.class)
    public String demoGlobalTransaction() {
        // 步骤 1：本地数据库操作（AT 模式自动记录 undo_log）
        log.info("步骤 1：执行本地数据库操作");

        // 步骤 2：通过 Feign 调用其他服务（被调服务同样引入 common-seata 即成为分支事务）
        log.info("步骤 2：通过 Feign 调用其他服务");

        // 任一步骤抛出异常，全局事务自动回滚
        return "全局事务执行完成";
    }
}
