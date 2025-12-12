package com.teambind.springproject.adapter.out.notification;

import com.teambind.springproject.application.port.out.NotificationSendPort;
import com.teambind.springproject.application.port.out.SmsPort;
import com.teambind.springproject.domain.model.notification.NotificationChannel;
import com.teambind.springproject.domain.model.notification.NotificationHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * SMS 알림 발송 어댑터
 */
@Component
@RequiredArgsConstructor
public class SmsNotificationSendAdapter implements NotificationSendPort {

    private final SmsPort smsPort;

    @Override
    public void send(NotificationHistory history) {
        smsPort.send(history.getRecipient(), history.getContent());
    }

    @Override
    public boolean supports(NotificationChannel channel) {
        return channel == NotificationChannel.SMS;
    }
}
