package com.teambind.springproject.adapter.out.persistence.notification;

import com.teambind.springproject.domain.model.notification.NotificationChannel;
import com.teambind.springproject.domain.model.notification.NotificationStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 알림 발송 이력 JPA Repository
 */
public interface NotificationHistoryJpaRepository extends JpaRepository<NotificationHistoryEntity, Long> {

    List<NotificationHistoryEntity> findByUserIdOrderByCreatedAtDesc(String userId);

    @Query("SELECT n FROM NotificationHistoryEntity n " +
            "WHERE n.status = 'RETRYING' AND n.nextRetryAt <= :before")
    List<NotificationHistoryEntity> findRetryableNotifications(@Param("before") LocalDateTime before);

    long countByStatus(NotificationStatus status);

    long countByChannel(NotificationChannel channel);

    @Query("SELECT n FROM NotificationHistoryEntity n " +
            "WHERE n.userId = :userId " +
            "AND n.createdAt >= :from AND n.createdAt <= :to " +
            "ORDER BY n.createdAt DESC")
    List<NotificationHistoryEntity> findByUserIdAndDateRange(
            @Param("userId") String userId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    List<NotificationHistoryEntity> findByStatusOrderByCreatedAtDesc(NotificationStatus status);
}
