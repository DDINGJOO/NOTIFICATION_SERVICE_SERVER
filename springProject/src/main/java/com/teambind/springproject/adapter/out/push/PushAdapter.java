package com.teambind.springproject.adapter.out.push;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.teambind.springproject.application.port.out.PushPort;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Firebase Cloud Messaging Adapter
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PushAdapter implements PushPort {

    private final Optional<FirebaseMessaging> firebaseMessaging;

    @Override
    public void send(String deviceToken, String title, String body) {
        send(deviceToken, title, body, null);
    }

    @Override
    public void send(String deviceToken, String title, String body, Map<String, String> data) {
        if (firebaseMessaging.isEmpty()) {
            log.warn("Firebase가 초기화되지 않아 Push 알림을 전송할 수 없습니다. deviceToken={}", deviceToken);
            return;
        }

        log.debug("Sending push notification to device: {}", deviceToken);

        try {
            Notification notification = Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build();

            Message.Builder messageBuilder = Message.builder()
                    .setToken(deviceToken)
                    .setNotification(notification);

            if (data != null && !data.isEmpty()) {
                messageBuilder.putAllData(data);
            }

            String response = firebaseMessaging.get().send(messageBuilder.build());
            log.info("Push notification sent successfully. messageId={}", response);

        } catch (FirebaseMessagingException e) {
            log.error("Failed to send push notification to device: {}", deviceToken, e);
            throw new RuntimeException("푸시 알림 전송 실패", e);
        }
    }
}
