package com.teambind.springproject.adapter.out.sms;

import com.teambind.springproject.application.port.out.SmsPort;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Solapi SMS Adapter
 */
@Slf4j
@Component
public class SmsAdapter implements SmsPort {

    private static final String SOLAPI_API_URL = "https://api.solapi.com/messages/v4/send";
    private static final int SMS_BYTE_LIMIT = 90;

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String apiSecret;
    private final String fromNumber;

    public SmsAdapter(
            @Value("${solapi.api-key}") String apiKey,
            @Value("${solapi.api-secret}") String apiSecret,
            @Value("${solapi.from-number}") String fromNumber) {
        this.restTemplate = new RestTemplate();
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.fromNumber = fromNumber;
    }

    @Override
    public void send(String to, String content) {
        log.debug("Sending SMS to: {}", to);

        try {
            HttpHeaders headers = createHeaders();
            Map<String, Object> body = createRequestBody(to, content);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            restTemplate.postForEntity(SOLAPI_API_URL, request, String.class);

            log.info("SMS sent successfully to: {}", to);

        } catch (Exception e) {
            log.error("Failed to send SMS to: {}", to, e);
            throw new RuntimeException("SMS 전송 실패", e);
        }
    }

    @Override
    public void sendVerificationSms(String to, String code) {
        String content = "[BANDER] 인증번호는 [" + code + "] 입니다.";
        send(to, content);
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", generateAuthorization());
        return headers;
    }

    private String generateAuthorization() {
        String date = ZonedDateTime.now(ZoneId.of("UTC"))
                .format(DateTimeFormatter.ISO_INSTANT);
        String salt = UUID.randomUUID().toString().replaceAll("-", "");
        String signature = generateSignature(date, salt);

        return String.format("HMAC-SHA256 apiKey=%s, date=%s, salt=%s, signature=%s",
                apiKey, date, salt, signature);
    }

    private String generateSignature(String date, String salt) {
        try {
            String message = date + salt;
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    apiSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Signature 생성 실패", e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private Map<String, Object> createRequestBody(String to, String content) {
        Map<String, Object> message = new HashMap<>();
        message.put("to", to);
        message.put("from", fromNumber);
        message.put("text", content);
        message.put("type", determineMessageType(content));

        Map<String, Object> body = new HashMap<>();
        body.put("message", message);
        body.put("messages", List.of(message));

        return body;
    }

    private String determineMessageType(String content) {
        int byteLength = content.getBytes(StandardCharsets.UTF_8).length;
        return byteLength <= SMS_BYTE_LIMIT ? "SMS" : "LMS";
    }
}
