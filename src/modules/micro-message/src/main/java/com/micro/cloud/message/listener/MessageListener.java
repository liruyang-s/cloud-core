package com.micro.cloud.message.listener;

import com.micro.cloud.common.rocketmq.core.IdempotentConsumerTemplate;
import com.micro.cloud.message.domain.MessageBody;
import com.micro.cloud.message.service.MessageSendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * 站内消息消费者（幂等消费）
 * <p>
 * RocketMQ 未部署时可通过 rocketmq.consumer.enabled=false 关闭，避免启动失败
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "rocketmq.consumer", name = "enabled", havingValue = "true", matchIfMissing = true)
@RocketMQMessageListener(topic = MessageSendService.TOPIC, consumerGroup = "micro-message-consumer-group")
public class MessageListener implements RocketMQListener<MessageBody> {

    private final IdempotentConsumerTemplate idempotentTemplate;

    @Override
    public void onMessage(MessageBody message) {
        idempotentTemplate.executeOnce(message.getMsgId(), Duration.ofHours(24), () -> {
            // 实际场景：落库站内信表、推送 WebSocket、调用短信/邮件渠道等
            log.info("消费站内消息: userId={}, title={}, type={}",
                    message.getUserId(), message.getTitle(), message.getType());
            return true;
        });
    }
}
