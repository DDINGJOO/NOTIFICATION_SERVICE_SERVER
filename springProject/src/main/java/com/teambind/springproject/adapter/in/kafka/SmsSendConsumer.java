package com.teambind.springproject.adapter.in.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teambind.springproject.application.port.in.SendSmsUseCase;
import com.teambind.springproject.application.port.in.SendSmsUseCase.SmsTarget;
import com.teambind.springproject.domain.event.SmsSendEvent;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * SMS 발송 Kafka Consumer
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SmsSendConsumer {

    private final SendSmsUseCase sendSmsUseCase;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "sms-send-request", groupId = "notification-consumer-group")
    public void consume(String message) {
        log.info("SMS 발송 이벤트 수신: {}", message);

        try {
            SmsSendEvent event = objectMapper.readValue(message, SmsSendEvent.class);

            List<SmsTarget> targets = event.targets().stream()
                    .map(t -> new SmsTarget(t.userId(), t.phoneNumber()))
                    .toList();

            sendSmsUseCase.sendSms(event.type(), targets, event.content());

            log.info("SMS 발송 이벤트 처리 완료. targetCount={}", targets.size());

        } catch (Exception e) {
            log.error("SMS 발송 이벤트 처리 실패", e);
        }
    }
}
