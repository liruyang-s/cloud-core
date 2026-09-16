package com.micro.cloud.common.core.utils;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * 钉钉机器人告警工具
 * <p>
 * 底座只封装推送能力，告警内容由业务方组装后调用 {@link #sendText(String)} /
 * {@link #sendMarkdown(String, String)} 发送。
 * <p>
 * 典型场景：Sentinel 限流触发、全局异常兜底、业务关键失败点。
 * <p>
 * 配置项（建议放 Nacos 配置中心，禁止硬编码）：
 * <pre>
 * alert:
 *   dingtalk:
 *     webhook: https://oapi.dingtalk.com/robot/send?access_token=xxx
 *     secret: SECxxx   # 加签密钥，可选
 * </pre>
 */
@Slf4j
public class AlertUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final String webhook;
    private final String secret;
    private final HttpClient httpClient;

    public AlertUtils(String webhook, String secret) {
        this.webhook = webhook;
        this.secret = secret;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    /**
     * 发送文本告警
     *
     * @param content 告警内容
     */
    public void sendText(String content) {
        if (StrUtil.isBlank(content)) {
            return;
        }
        Map<String, Object> body = new HashMap<>();
        body.put("msgtype", "text");
        Map<String, String> text = new HashMap<>();
        text.put("content", content);
        body.put("text", text);
        doSend(body);
    }

    /**
     * 发送 Markdown 告警
     *
     * @param title 标题
     * @param text  Markdown 内容
     */
    public void sendMarkdown(String title, String text) {
        if (StrUtil.isBlank(text)) {
            return;
        }
        Map<String, Object> body = new HashMap<>();
        body.put("msgtype", "markdown");
        Map<String, String> markdown = new HashMap<>();
        markdown.put("title", title);
        markdown.put("text", text);
        body.put("markdown", markdown);
        doSend(body);
    }

    /**
     * 执行推送（异步，失败仅记录日志，不影响主流程）
     */
    private void doSend(Map<String, Object> body) {
        if (StrUtil.isBlank(webhook)) {
            log.warn("钉钉告警 webhook 未配置，跳过推送: {}", body);
            return;
        }
        try {
            String url = signUrl(webhook, secret);
            String json = MAPPER.writeValueAsString(body);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(5))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                log.warn("钉钉告警推送失败: status={}, body={}", response.statusCode(), response.body());
            }
        } catch (Exception e) {
            log.error("钉钉告警推送异常", e);
        }
    }

    /**
     * 加签（钉钉机器人安全设置-加签模式）
     */
    private String signUrl(String webhook, String secret) throws Exception {
        if (StrUtil.isBlank(secret)) {
            return webhook;
        }
        long timestamp = System.currentTimeMillis();
        String stringToSign = timestamp + "\n" + secret;
        javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
        mac.init(new javax.crypto.spec.SecretKeySpec(secret.getBytes(java.nio.charset.StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] signData = mac.doFinal(stringToSign.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        String sign = java.net.URLEncoder.encode(java.util.Base64.getEncoder().encodeToString(signData),
                java.nio.charset.StandardCharsets.UTF_8);
        return webhook + "&timestamp=" + timestamp + "&sign=" + sign;
    }
}
