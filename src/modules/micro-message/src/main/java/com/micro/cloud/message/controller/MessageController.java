package com.micro.cloud.message.controller;

import com.micro.cloud.common.core.domain.Result;
import com.micro.cloud.message.domain.MessageBody;
import com.micro.cloud.message.service.MessageSendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 消息发送
 */
@Tag(name = "消息发送")
@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageSendService messageSendService;

    @Operation(summary = "发送站内消息")
    @PostMapping("/send")
    public Result<String> send(@Valid @RequestBody MessageBody body) {
        return Result.success(messageSendService.send(body));
    }

    @Operation(summary = "发送延时消息")
    @PostMapping("/sendDelay")
    public Result<String> sendDelay(@Valid @RequestBody MessageBody body,
                                    @RequestParam(defaultValue = "5") int delayLevel) {
        return Result.success(messageSendService.sendDelay(body, delayLevel));
    }
}
