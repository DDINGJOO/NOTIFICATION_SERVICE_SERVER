package com.teambind.springproject.adapter.out.persistence.notification;

import com.teambind.springproject.domain.model.notification.FailureReason;
import com.teambind.springproject.domain.model.notification.NotificationChannel;
import com.teambind.springproject.domain.model.notification.NotificationHistory;
import com.teambind.springproject.domain.model.notification.NotificationStatus;
import com.teambind.springproject.domain.model.notification.NotificationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 알림 발송 이력 JPA Entity
 */
@Entity
@Table(name = "notification_history", indexes = {
        @Index(name = "idx_notification_history_user_id", columnList = "user_id"),
        @Index(name = "idx_notification_history_status", columnList = "status"),
        @Index(name = "idx_notification_history_channel", columnList = "channel"),
        @Index(name = "idx_notification_history_created_at", columnList = "created_at")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class NotificationHistoryEntity {

    @Id
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "recipient", nullable = false)
    private String recipient;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false)
    private NotificationChannel channel;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotificationType type;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private NotificationStatus status;

    @Column(name = "failure_type")
    private String failureType;

    @Column(name = "failure_message")
    private String failureMessage;

    @Column(name = "retry_count", nullable = false)
    private int retryCount;

    @Column(name = "next_retry_at")
    private LocalDateTime nextRetryAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    private NotificationHistoryEntity(Long id, String userId, String recipient,
                                      NotificationChannel channel, NotificationType type,
                                      String content, NotificationStatus status,
                                      String failureType, String failureMessage,
                                      int retryCount, LocalDateTime nextRetryAt,
                                      LocalDateTime sentAt) {
        this.id = id;
        this.userId = userId;
        this.recipient = recipient;
        this.channel = channel;
        this.type = type;
        this.content = content;
        this.status = status;
        this.failureType = failureType;
        this.failureMessage = failureMessage;
        this.retryCount = retryCount;
        this.nextRetryAt = nextRetryAt;
        this.sentAt = sentAt;
    }

    /**
     * 도메인 모델로부터 Entity 생성
     */
    public static NotificationHistoryEntity fromDomain(NotificationHistory history) {
        String failureType = null;
        String failureMessage = null;
        if (history.getFailureReason() != null) {
            failureType = history.getFailureReason().getType().name();
            failureMessage = history.getFailureReason().getMessage();
        }

        NotificationHistoryEntity entity = new NotificationHistoryEntity(
                history.getId(),
                history.getUserId(),
                history.getRecipient(),
                history.getChannel(),
                history.getType(),
                history.getContent(),
                history.getStatus(),
                failureType,
                failureMessage,
                history.getRetryCount(),
                history.getNextRetryAt(),
                history.getSentAt()
        );
        entity.createdAt = history.getCreatedAt();
        entity.updatedAt = history.getUpdatedAt();
        return entity;
    }

    /**
     * 도메인 모델로 변환
     */
    public NotificationHistory toDomain() {
        FailureReason failureReason = null;
        if (failureType != null) {
            FailureReason.FailureType type = FailureReason.FailureType.valueOf(failureType);
            failureReason = FailureReason.of(type, failureMessage);
        }

        return NotificationHistory.restore(
                id,
                userId,
                recipient,
                channel,
                type,
                content,
                status,
                failureReason,
                retryCount,
                nextRetryAt,
                createdAt,
                updatedAt,
                sentAt
        );
    }

    /**
     * 상태 업데이트
     */
    public void updateStatus(NotificationStatus status, FailureReason failureReason,
                             int retryCount, LocalDateTime nextRetryAt, LocalDateTime sentAt) {
        this.status = status;
        if (failureReason != null) {
            this.failureType = failureReason.getType().name();
            this.failureMessage = failureReason.getMessage();
        }
        this.retryCount = retryCount;
        this.nextRetryAt = nextRetryAt;
        this.sentAt = sentAt;
    }
}
