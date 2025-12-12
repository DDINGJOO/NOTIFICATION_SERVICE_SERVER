package com.teambind.springproject.adapter.out.persistence.notification;

import com.teambind.springproject.application.port.out.NotificationHistoryPort;
import com.teambind.springproject.domain.model.notification.NotificationChannel;
import com.teambind.springproject.domain.model.notification.NotificationHistory;
import com.teambind.springproject.domain.model.notification.NotificationStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * 알림 발송 이력 Persistence Adapter
 */
@Repository
@RequiredArgsConstructor
public class NotificationHistoryPersistenceAdapter implements NotificationHistoryPort {

    private final NotificationHistoryJpaRepository jpaRepository;

    @Override
    public NotificationHistory save(NotificationHistory history) {
        NotificationHistoryEntity entity = NotificationHistoryEntity.fromDomain(history);
        NotificationHistoryEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<NotificationHistory> findById(Long id) {
        return jpaRepository.findById(id)
                .map(NotificationHistoryEntity::toDomain);
    }

    @Override
    public List<NotificationHistory> findByUserId(String userId) {
        return jpaRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(NotificationHistoryEntity::toDomain)
                .toList();
    }

    @Override
    public List<NotificationHistory> findRetryableNotifications(LocalDateTime before) {
        return jpaRepository.findRetryableNotifications(before).stream()
                .map(NotificationHistoryEntity::toDomain)
                .toList();
    }

    @Override
    public long countByStatus(NotificationStatus status) {
        return jpaRepository.countByStatus(status);
    }

    @Override
    public long countByChannel(NotificationChannel channel) {
        return jpaRepository.countByChannel(channel);
    }

    @Override
    public List<NotificationHistory> findByUserIdAndDateRange(String userId, LocalDateTime from, LocalDateTime to) {
        return jpaRepository.findByUserIdAndDateRange(userId, from, to).stream()
                .map(NotificationHistoryEntity::toDomain)
                .toList();
    }

    @Override
    public List<NotificationHistory> findDeadLetterQueue() {
        return jpaRepository.findByStatusOrderByCreatedAtDesc(NotificationStatus.DEAD_LETTER).stream()
                .map(NotificationHistoryEntity::toDomain)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
