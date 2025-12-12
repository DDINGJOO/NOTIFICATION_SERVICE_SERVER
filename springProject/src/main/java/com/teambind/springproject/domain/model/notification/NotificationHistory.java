package com.teambind.springproject.domain.model.notification;

import java.time.LocalDateTime;
import lombok.Getter;

/**
 * 알림 발송 이력 Aggregate Root
 */
@Getter
public class NotificationHistory {

    private static final int MAX_RETRY_COUNT = 3;

    private final Long id;
    private final String userId;
    private final String recipient;
    private final NotificationChannel channel;
    private final NotificationType type;
    private final String content;
    private NotificationStatus status;
    private FailureReason failureReason;
    private int retryCount;
    private LocalDateTime nextRetryAt;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime sentAt;

    private NotificationHistory(Long id, String userId, String recipient,
                                NotificationChannel channel, NotificationType type,
                                String content, NotificationStatus status,
                                FailureReason failureReason, int retryCount,
                                LocalDateTime nextRetryAt, LocalDateTime createdAt,
                                LocalDateTime updatedAt, LocalDateTime sentAt) {
        this.id = id;
        this.userId = userId;
        this.recipient = recipient;
        this.channel = channel;
        this.type = type;
        this.content = content;
        this.status = status;
        this.failureReason = failureReason;
        this.retryCount = retryCount;
        this.nextRetryAt = nextRetryAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.sentAt = sentAt;
    }

    /**
     * 신규 발송 이력 생성
     */
    public static NotificationHistory create(Long id, String userId, String recipient,
                                             NotificationChannel channel, NotificationType type,
                                             String content) {
        LocalDateTime now = LocalDateTime.now();
        return new NotificationHistory(
                id, userId, recipient, channel, type, content,
                NotificationStatus.PENDING, null, 0, null, now, now, null
        );
    }

    /**
     * DB 조회 결과로부터 복원
     */
    public static NotificationHistory restore(Long id, String userId, String recipient,
                                              NotificationChannel channel, NotificationType type,
                                              String content, NotificationStatus status,
                                              FailureReason failureReason, int retryCount,
                                              LocalDateTime nextRetryAt, LocalDateTime createdAt,
                                              LocalDateTime updatedAt, LocalDateTime sentAt) {
        return new NotificationHistory(
                id, userId, recipient, channel, type, content,
                status, failureReason, retryCount, nextRetryAt,
                createdAt, updatedAt, sentAt
        );
    }

    /**
     * 발송 성공 처리
     */
    public void markAsSuccess() {
        this.status = NotificationStatus.SUCCESS;
        this.sentAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 발송 실패 처리
     */
    public void markAsFailed(FailureReason reason) {
        this.failureReason = reason;
        this.updatedAt = LocalDateTime.now();

        if (reason.isRetryable() && canRetry()) {
            this.status = NotificationStatus.RETRYING;
            this.retryCount++;
            this.nextRetryAt = calculateNextRetryTime();
        } else if (!reason.isRetryable()) {
            this.status = NotificationStatus.FAILED;
        } else {
            this.status = NotificationStatus.DEAD_LETTER;
        }
    }

    /**
     * 재시도 가능 여부
     */
    public boolean canRetry() {
        return retryCount < MAX_RETRY_COUNT;
    }

    /**
     * 재시도 대기 상태 여부
     */
    public boolean isWaitingForRetry() {
        return status == NotificationStatus.RETRYING && nextRetryAt != null;
    }

    /**
     * 재시도 가능 시간 여부
     */
    public boolean isReadyForRetry() {
        return isWaitingForRetry() && LocalDateTime.now().isAfter(nextRetryAt);
    }

    /**
     * Exponential Backoff: 1분, 5분, 15분
     */
    private LocalDateTime calculateNextRetryTime() {
        int[] backoffMinutes = {1, 5, 15};
        int index = Math.min(retryCount - 1, backoffMinutes.length - 1);
        return LocalDateTime.now().plusMinutes(backoffMinutes[index]);
    }

    /**
     * 트랜잭션 알림 여부 (동의 확인 불필요)
     */
    public boolean isTransactional() {
        return type == NotificationType.TRANSACTIONAL;
    }

    /**
     * 마케팅 알림 여부 (동의 확인 필요)
     */
    public boolean isMarketing() {
        return type == NotificationType.MARKETING;
    }

    /**
     * Dead Letter 상태 여부
     */
    public boolean isDeadLetter() {
        return status == NotificationStatus.DEAD_LETTER;
    }

    /**
     * DLQ 수동 재처리를 위한 상태 리셋
     */
    public void resetForReprocess() {
        this.status = NotificationStatus.RETRYING;
        this.retryCount = 0;
        this.nextRetryAt = LocalDateTime.now();
        this.failureReason = null;
        this.updatedAt = LocalDateTime.now();
    }
}
