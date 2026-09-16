package com.micro.cloud.message.service;

import cn.hutool.core.util.IdUtil;
import com.micro.cloud.common.rocketmq.utils.MessageProducer;
import com.micro.cloud.message.domain.MessageBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 消息发送服务
 * <p>
 * 业务方调用发送接口，消息经 RocketMQ 异步投递，消费端落库/推送，
 * 实现业务与消息通知的解耦削峰。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageSendService {

    private final MessageProducer messageProducer;

    public static final String TOPIC = "micro-message-topic";

    /** 发送站内消息 */
    public String send(MessageBody body) {
        String msgId = IdUtil.fastSimpleUUID();
        body.setMsgId(msgId);
        messageProducer.send(TOPIC, body);
        return msgId;
    }

    /** 发送延时消息（如订单超时提醒） */
    public String sendDelay(MessageBody body, int delayLevel) {
        String msgId = IdUtil.fastSimpleUUID();
        body.setMsgId(msgId);
        messageProducer.sendDelay(TOPIC, body, delayLevel);
        return msgId;
    }
}
