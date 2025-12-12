package com.teambind.springproject.adapter.in.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teambind.springproject.application.port.in.SendPushUseCase;
import com.teambind.springproject.application.port.in.SendPushUseCase.PushTarget;
import com.teambind.springproject.domain.event.PushSendEvent;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 푸시 알림 발송 Kafka Consumer
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PushSendConsumer {

    private final SendPushUseCase sendPushUseCase;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "push-send-request", groupId = "notification-consumer-group")
    public void consume(String message) {
        log.info("푸시 알림 발송 이벤트 수신: {}", message);

        try {
            PushSendEvent event = objectMapper.readValue(message, PushSendEvent.class);

            List<PushTarget> targets = event.targets().stream()
                    .map(t -> new PushTarget(t.userId(), t.deviceToken()))
                    .toList();

            sendPushUseCase.sendPush(event.type(), targets, event.title(), event.body(), event.data());

            log.info("푸시 알림 발송 이벤트 처리 완료. targetCount={}", targets.size());

        } catch (Exception e) {
            log.error("푸시 알림 발송 이벤트 처리 실패", e);
        }
    }
}
