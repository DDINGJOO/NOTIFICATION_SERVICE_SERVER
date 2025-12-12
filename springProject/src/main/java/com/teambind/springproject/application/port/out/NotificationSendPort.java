package com.teambind.springproject.application.port.out;

import com.teambind.springproject.domain.model.notification.NotificationChannel;
import com.teambind.springproject.domain.model.notification.NotificationHistory;

/**
 * 알림 발송 통합 Outbound Port
 */
public interface NotificationSendPort {

    /**
     * 알림 발송
     *
     * @param history 발송할 알림 이력
     */
    void send(NotificationHistory history);

    /**
     * 지원하는 채널인지 확인
     *
     * @param channel 알림 채널
     * @return 지원 여부
     */
    boolean supports(NotificationChannel channel);
}
