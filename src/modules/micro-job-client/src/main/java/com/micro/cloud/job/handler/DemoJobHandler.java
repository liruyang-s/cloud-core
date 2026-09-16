package com.micro.cloud.job.handler;

import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 示例任务处理器
 * <p>
 * 底座仅提供示例，业务任务逻辑由业务服务自行编写：
 * 复制本类到业务模块，修改 handler 名称与任务逻辑即可。
 * <p>
 * 调度中心新建任务时，JobHandler 填写 {@code @XxlJob} 注解中的名称（如 demoJob）。
 */
@Slf4j
@Component
public class DemoJobHandler {

    /**
     * 简单任务示例（Bean 模式）
     */
    @XxlJob("demoJob")
    public void demoJob() {
        log.info("XXL-Job 示例任务执行");
    }
}
