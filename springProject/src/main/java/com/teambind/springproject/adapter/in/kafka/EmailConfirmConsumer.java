package com.teambind.springproject.adapter.in.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teambind.springproject.application.port.out.EmailPort;
import com.teambind.springproject.domain.event.EmailConfirmEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailConfirmConsumer {

    private final EmailPort emailPort;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "email-confirm-request",
            groupId = "notification-consumer-group"
    )
    public void consume(String message) {
        log.debug("Received email-confirm-request: {}", message);

        try {
            EmailConfirmEvent event = objectMapper.readValue(message, EmailConfirmEvent.class);
            emailPort.sendVerificationEmail(event.getEmail(), event.getCode());
            log.info("Verification email sent to: {}", event.getEmail());

        } catch (Exception e) {
            log.error("Failed to process email-confirm-request: {}", message, e);
            throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
        }
    }
}
