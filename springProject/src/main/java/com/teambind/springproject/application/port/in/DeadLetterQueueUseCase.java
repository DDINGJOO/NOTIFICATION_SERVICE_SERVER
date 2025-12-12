package com.teambind.springproject.application.port.in;

import com.teambind.springproject.domain.model.notification.NotificationHistory;
import java.util.List;

/**
 * Dead Letter Queue 처리 Inbound Port
 */
public interface DeadLetterQueueUseCase {

    /**
     * DLQ 목록 조회
     */
    List<NotificationHistory> getDeadLetterQueue();

    /**
     * DLQ 항목 수동 재처리
     */
    void reprocessDeadLetter(Long notificationId);

    /**
     * DLQ 항목 삭제 (포기)
     */
    void discardDeadLetter(Long notificationId);
}
