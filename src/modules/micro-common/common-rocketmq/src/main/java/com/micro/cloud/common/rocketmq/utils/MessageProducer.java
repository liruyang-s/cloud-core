package com.micro.cloud.common.rocketmq.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.time.Duration;

/**
 * 消息生产工具：普通消息、延时消息统一发送入口
 */
@Slf4j
@RequiredArgsConstructor
public class MessageProducer {

    private final RocketMQTemplate rocketMQTemplate;

    /** 发送普通消息 */
    public void send(String topic, Object payload) {
        rocketMQTemplate.convertAndSend(topic, payload);
        log.info("发送普通消息成功, topic={}", topic);
    }

    /**
     * 发送延时消息
     *
     * @param delayLevel RocketMQ 延时级别（1~18，对应 1s/5s/10s/30s/1m/2m/3m/4m/5m/6m/7m/8m/9m/10m/20m/30m/1h/2h）
     */
    public void sendDelay(String topic, Object payload, int delayLevel) {
        Message<?> message = MessageBuilder.withPayload(payload).build();
        rocketMQTemplate.syncSend(topic, message, Duration.ofSeconds(3).toMillis(), delayLevel);
        log.info("发送延时消息成功, topic={}, delayLevel={}", topic, delayLevel);
    }
}
