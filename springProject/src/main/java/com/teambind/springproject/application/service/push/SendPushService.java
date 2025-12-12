package com.teambind.springproject.application.service.push;

import com.teambind.springproject.application.port.in.SendPushUseCase;
import com.teambind.springproject.application.port.out.NotificationHistoryPort;
import com.teambind.springproject.application.port.out.PushPort;
import com.teambind.springproject.common.util.generator.PrimaryKeyGenerator;
import com.teambind.springproject.domain.model.notification.FailureReason;
import com.teambind.springproject.domain.model.notification.NotificationChannel;
import com.teambind.springproject.domain.model.notification.NotificationHistory;
import com.teambind.springproject.domain.model.notification.NotificationType;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 푸시 알림 발송 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SendPushService implements SendPushUseCase {

    private final PushPort pushPort;
    private final NotificationHistoryPort historyPort;
    private final PrimaryKeyGenerator keyGenerator;

    @Override
    @Transactional
    public Long sendPush(String userId, String deviceToken, String title, String body) {
        return sendPush(userId, deviceToken, title, body, null);
    }

    @Override
    @Transactional
    public Long sendPush(String userId, String deviceToken, String title, String body, Map<String, String> data) {
        log.info("푸시 알림 발송 요청. userId={}, deviceToken={}", userId, deviceToken);

        Long historyId = keyGenerator.generateLongKey();
        String content = formatContent(title, body);

        NotificationHistory history = NotificationHistory.create(
                historyId,
                userId,
                deviceToken,
                NotificationChannel.PUSH,
                NotificationType.TRANSACTIONAL,
                content
        );

        try {
            pushPort.send(deviceToken, title, body, data);
            history.markAsSuccess();
            log.info("푸시 알림 발송 성공. historyId={}", historyId);
        } catch (Exception e) {
            log.error("푸시 알림 발송 실패. historyId={}", historyId, e);
            history.markAsFailed(FailureReason.providerError(e.getMessage()));
        }

        historyPort.save(history);
        return historyId;
    }

    private String formatContent(String title, String body) {
        return String.format("[%s] %s", title, body);
    }
}
