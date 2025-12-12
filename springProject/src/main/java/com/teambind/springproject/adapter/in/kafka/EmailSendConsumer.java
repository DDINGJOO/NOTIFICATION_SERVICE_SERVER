package com.teambind.springproject.adapter.in.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teambind.springproject.application.port.in.SendEmailUseCase;
import com.teambind.springproject.application.port.in.SendEmailUseCase.EmailTarget;
import com.teambind.springproject.domain.event.EmailSendEvent;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 이메일 발송 Kafka Consumer (일괄 발송용)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailSendConsumer {

    private final SendEmailUseCase sendEmailUseCase;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "email-send-request", groupId = "notification-consumer-group")
    public void consume(String message) {
        log.info("이메일 발송 이벤트 수신: {}", message);

        try {
            EmailSendEvent event = objectMapper.readValue(message, EmailSendEvent.class);

            List<EmailTarget> targets = event.targets().stream()
                    .map(t -> new EmailTarget(t.userId(), t.email()))
                    .toList();

            sendEmailUseCase.sendEmail(event.type(), targets, event.title(), event.content());

            log.info("이메일 발송 이벤트 처리 완료. targetCount={}", targets.size());

        } catch (Exception e) {
            log.error("이메일 발송 이벤트 처리 실패", e);
        }
    }
}
