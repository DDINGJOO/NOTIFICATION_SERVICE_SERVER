package com.teambind.springproject.application.port.in;

/**
 * 알림 재시도 Inbound Port
 */
public interface NotificationRetryUseCase {

    /**
     * 재시도 대기 중인 알림 처리
     */
    void processRetryableNotifications();

    /**
     * 특정 알림 재시도
     */
    void retryNotification(Long notificationId);
}
