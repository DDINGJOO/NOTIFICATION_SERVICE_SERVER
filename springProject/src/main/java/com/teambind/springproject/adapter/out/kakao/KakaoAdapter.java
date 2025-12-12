package com.teambind.springproject.adapter.out.kakao;

import com.teambind.springproject.application.port.out.KakaoPort;
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
 * Solapi 카카오 알림톡 Adapter
 */
@Slf4j
@Component
public class KakaoAdapter implements KakaoPort {

    private static final String SOLAPI_API_URL = "https://api.solapi.com/messages/v4/send";

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String apiSecret;
    private final String pfId;
    private final String fromNumber;

    public KakaoAdapter(
            @Value("${solapi.api-key}") String apiKey,
            @Value("${solapi.api-secret}") String apiSecret,
            @Value("${solapi.kakao.pf-id:}") String pfId,
            @Value("${solapi.from-number}") String fromNumber) {
        this.restTemplate = new RestTemplate();
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.pfId = pfId;
        this.fromNumber = fromNumber;
    }

    @Override
    public void send(String phoneNumber, String templateCode, Map<String, String> variables) {
        log.debug("Sending Kakao AlimTalk to: {}, templateCode: {}", phoneNumber, templateCode);

        try {
            HttpHeaders headers = createHeaders();
            Map<String, Object> body = createRequestBody(phoneNumber, templateCode, variables);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            restTemplate.postForEntity(SOLAPI_API_URL, request, String.class);

            log.info("Kakao AlimTalk sent successfully to: {}", phoneNumber);

        } catch (Exception e) {
            log.error("Failed to send Kakao AlimTalk to: {}", phoneNumber, e);
            throw new RuntimeException("알림톡 전송 실패", e);
        }
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

    private Map<String, Object> createRequestBody(String phoneNumber, String templateCode, Map<String, String> variables) {
        Map<String, Object> kakaoOptions = new HashMap<>();
        kakaoOptions.put("pfId", pfId);
        kakaoOptions.put("templateId", templateCode);
        if (variables != null && !variables.isEmpty()) {
            kakaoOptions.put("variables", variables);
        }

        Map<String, Object> message = new HashMap<>();
        message.put("to", phoneNumber);
        message.put("from", fromNumber);
        message.put("type", "ATA"); // AlimTalk
        message.put("kakaoOptions", kakaoOptions);

        Map<String, Object> body = new HashMap<>();
        body.put("message", message);
        body.put("messages", List.of(message));

        return body;
    }
}
