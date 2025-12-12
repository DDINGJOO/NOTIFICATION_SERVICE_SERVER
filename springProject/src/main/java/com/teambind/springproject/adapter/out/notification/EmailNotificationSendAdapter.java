package com.teambind.springproject.adapter.out.notification;

import com.teambind.springproject.application.port.out.EmailPort;
import com.teambind.springproject.application.port.out.NotificationSendPort;
import com.teambind.springproject.domain.model.notification.NotificationChannel;
import com.teambind.springproject.domain.model.notification.NotificationHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 이메일 알림 발송 어댑터
 */
@Component
@RequiredArgsConstructor
public class EmailNotificationSendAdapter implements NotificationSendPort {

    private final EmailPort emailPort;

    @Override
    public void send(NotificationHistory history) {
        emailPort.send(history.getRecipient(), "알림", history.getContent());
    }

    @Override
    public boolean supports(NotificationChannel channel) {
        return channel == NotificationChannel.EMAIL;
    }
}
