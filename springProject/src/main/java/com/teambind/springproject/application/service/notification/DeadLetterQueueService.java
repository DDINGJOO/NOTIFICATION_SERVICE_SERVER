package com.teambind.springproject.application.service.notification;

import com.teambind.springproject.application.port.in.DeadLetterQueueUseCase;
import com.teambind.springproject.application.port.out.NotificationHistoryPort;
import com.teambind.springproject.domain.model.notification.NotificationHistory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Dead Letter Queue 처리 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeadLetterQueueService implements DeadLetterQueueUseCase {

    private final NotificationHistoryPort historyPort;

    @Override
    @Transactional(readOnly = true)
    public List<NotificationHistory> getDeadLetterQueue() {
        return historyPort.findDeadLetterQueue();
    }

    @Override
    @Transactional
    public void reprocessDeadLetter(Long notificationId) {
        NotificationHistory history = historyPort.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("알림 이력을 찾을 수 없습니다. id=" + notificationId));

        if (!history.isDeadLetter()) {
            throw new IllegalStateException("Dead Letter 상태가 아닌 알림입니다. id=" + notificationId);
        }

        log.info("DLQ 수동 재처리 시작. notificationId={}", notificationId);
        history.resetForReprocess();
        historyPort.save(history);
        log.info("DLQ 항목 재처리 큐에 추가됨. notificationId={}", notificationId);
    }

    @Override
    @Transactional
    public void discardDeadLetter(Long notificationId) {
        NotificationHistory history = historyPort.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("알림 이력을 찾을 수 없습니다. id=" + notificationId));

        if (!history.isDeadLetter()) {
            throw new IllegalStateException("Dead Letter 상태가 아닌 알림입니다. id=" + notificationId);
        }

        log.info("DLQ 항목 폐기. notificationId={}", notificationId);
        historyPort.deleteById(notificationId);
    }
}
