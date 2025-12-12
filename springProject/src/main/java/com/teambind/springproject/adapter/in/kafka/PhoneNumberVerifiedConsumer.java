package com.teambind.springproject.adapter.in.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teambind.springproject.application.port.in.UserConsentUseCase;
import com.teambind.springproject.domain.event.PhoneNumberVerifiedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 전화번호 인증 완료 Kafka Consumer
 * Topic: phone-number-verified
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PhoneNumberVerifiedConsumer {

    private final UserConsentUseCase userConsentUseCase;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "phone-number-verified",
            groupId = "notification-consumer-group"
    )
    public void consume(String message) {
        log.debug("Received phone-number-verified: {}", message);

        try {
            PhoneNumberVerifiedEvent event = objectMapper.readValue(message, PhoneNumberVerifiedEvent.class);
            userConsentUseCase.handlePhoneNumberVerified(event.getUserId(), event.getPhoneNumber());
            log.info("Phone number verified processed: userId={}", event.getUserId());

        } catch (Exception e) {
            log.error("Failed to process phone-number-verified: {}", message, e);
            throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
        }
    }
}
