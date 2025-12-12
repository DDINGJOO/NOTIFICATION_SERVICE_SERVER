package com.teambind.springproject.application.port.out;

import com.teambind.springproject.domain.model.notification.NotificationChannel;
import com.teambind.springproject.domain.model.notification.NotificationHistory;
import com.teambind.springproject.domain.model.notification.NotificationStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 알림 발송 이력 Outbound Port
 */
public interface NotificationHistoryPort {

    /**
     * 이력 저장
     */
    NotificationHistory save(NotificationHistory history);

    /**
     * ID로 조회
     */
    Optional<NotificationHistory> findById(Long id);

    /**
     * 사용자 ID로 이력 조회
     */
    List<NotificationHistory> findByUserId(String userId);

    /**
     * 재시도 대기 중인 이력 조회
     */
    List<NotificationHistory> findRetryableNotifications(LocalDateTime before);

    /**
     * 상태별 이력 수 조회
     */
    long countByStatus(NotificationStatus status);

    /**
     * 채널별 이력 수 조회
     */
    long countByChannel(NotificationChannel channel);

    /**
     * 기간별 이력 조회
     */
    List<NotificationHistory> findByUserIdAndDateRange(String userId, LocalDateTime from, LocalDateTime to);

    /**
     * Dead Letter Queue 조회
     */
    List<NotificationHistory> findDeadLetterQueue();

    /**
     * 이력 삭제
     */
    void deleteById(Long id);
}
