package com.teambind.springproject.adapter.in.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teambind.springproject.application.port.out.SmsPort;
import com.teambind.springproject.domain.event.SmsConfirmEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * SMS 인증 요청 Kafka Consumer
 * Topic: sms-confirm-request
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SmsConfirmConsumer {

    private final SmsPort smsPort;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "sms-confirm-request",
            groupId = "notification-consumer-group"
    )
    public void consume(String message) {
        log.debug("Received sms-confirm-request: {}", message);

        try {
            SmsConfirmEvent event = objectMapper.readValue(message, SmsConfirmEvent.class);
            smsPort.sendVerificationSms(event.getPhoneNumber(), event.getCode());
            log.info("Verification SMS sent to: {}", event.getPhoneNumber());

        } catch (Exception e) {
            log.error("Failed to process sms-confirm-request: {}", message, e);
            throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
        }
    }
}
