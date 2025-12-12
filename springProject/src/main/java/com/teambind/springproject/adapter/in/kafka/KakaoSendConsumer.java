package com.teambind.springproject.adapter.in.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teambind.springproject.application.port.in.SendKakaoUseCase;
import com.teambind.springproject.application.port.in.SendKakaoUseCase.KakaoTarget;
import com.teambind.springproject.domain.event.KakaoSendEvent;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 카카오 알림톡 발송 Kafka Consumer
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoSendConsumer {

    private final SendKakaoUseCase sendKakaoUseCase;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "kakao-send-request", groupId = "notification-consumer-group")
    public void consume(String message) {
        log.info("알림톡 발송 이벤트 수신: {}", message);

        try {
            KakaoSendEvent event = objectMapper.readValue(message, KakaoSendEvent.class);

            List<KakaoTarget> targets = event.targets().stream()
                    .map(t -> new KakaoTarget(t.userId(), t.phoneNumber()))
                    .toList();

            sendKakaoUseCase.sendKakao(event.type(), targets, event.templateCode(), event.variables());

            log.info("알림톡 발송 이벤트 처리 완료. targetCount={}", targets.size());

        } catch (Exception e) {
            log.error("알림톡 발송 이벤트 처리 실패", e);
        }
    }
}
