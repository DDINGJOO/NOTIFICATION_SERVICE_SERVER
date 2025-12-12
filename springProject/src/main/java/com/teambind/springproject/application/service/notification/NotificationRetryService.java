package com.teambind.springproject.application.service.notification;

import com.teambind.springproject.application.port.in.NotificationRetryUseCase;
import com.teambind.springproject.application.port.out.NotificationHistoryPort;
import com.teambind.springproject.application.port.out.NotificationSendPort;
import com.teambind.springproject.domain.model.notification.FailureReason;
import com.teambind.springproject.domain.model.notification.NotificationHistory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 알림 재시도 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationRetryService implements NotificationRetryUseCase {

    private final NotificationHistoryPort historyPort;
    private final List<NotificationSendPort> sendPorts;

    @Override
    @Transactional
    public void processRetryableNotifications() {
        List<NotificationHistory> retryableNotifications =
                historyPort.findRetryableNotifications(LocalDateTime.now());

        log.info("재시도 대상 알림 {}건 처리 시작", retryableNotifications.size());

        for (NotificationHistory history : retryableNotifications) {
            try {
                retryNotification(history);
            } catch (Exception e) {
                log.error("알림 재시도 중 오류 발생. notificationId={}", history.getId(), e);
            }
        }
    }

    @Override
    @Transactional
    public void retryNotification(Long notificationId) {
        NotificationHistory history = historyPort.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("알림 이력을 찾을 수 없습니다. id=" + notificationId));

        if (!history.isReadyForRetry()) {
            log.warn("재시도 불가능한 알림. notificationId={}, status={}", notificationId, history.getStatus());
            return;
        }

        retryNotification(history);
    }

    private void retryNotification(NotificationHistory history) {
        NotificationSendPort sendPort = findSendPort(history);

        if (sendPort == null) {
            log.error("지원하지 않는 채널. channel={}", history.getChannel());
            history.markAsFailed(FailureReason.unknown("지원하지 않는 알림 채널"));
            historyPort.save(history);
            return;
        }

        try {
            log.info("알림 재시도 시작. notificationId={}, retryCount={}", history.getId(), history.getRetryCount());
            sendPort.send(history);
            history.markAsSuccess();
            log.info("알림 재시도 성공. notificationId={}", history.getId());
        } catch (Exception e) {
            log.error("알림 재시도 실패. notificationId={}", history.getId(), e);
            history.markAsFailed(FailureReason.providerError(e.getMessage()));
        }

        historyPort.save(history);
    }

    private NotificationSendPort findSendPort(NotificationHistory history) {
        return sendPorts.stream()
                .filter(port -> port.supports(history.getChannel()))
                .findFirst()
                .orElse(null);
    }
}
