package com.teambind.springproject.adapter.out.notification;

import com.teambind.springproject.application.port.out.NotificationSendPort;
import com.teambind.springproject.application.port.out.PushPort;
import com.teambind.springproject.domain.model.notification.NotificationChannel;
import com.teambind.springproject.domain.model.notification.NotificationHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 푸시 알림 발송 어댑터 (재시도용)
 */
@Component
@RequiredArgsConstructor
public class PushNotificationSendAdapter implements NotificationSendPort {

    private final PushPort pushPort;

    @Override
    public void send(NotificationHistory history) {
        // content 형식: [title] body
        String content = history.getContent();
        String title = "알림";
        String body = content;

        if (content.startsWith("[") && content.contains("]")) {
            int endIndex = content.indexOf("]");
            title = content.substring(1, endIndex);
            body = content.substring(endIndex + 2);
        }

        pushPort.send(history.getRecipient(), title, body);
    }

    @Override
    public boolean supports(NotificationChannel channel) {
        return channel == NotificationChannel.PUSH;
    }
}
