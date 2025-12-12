package com.teambind.springproject.domain.event;

import com.teambind.springproject.domain.model.notification.NotificationType;
import java.util.List;
import java.util.Map;

/**
 * 푸시 알림 발송 이벤트
 */
public record PushSendEvent(
        NotificationType type,
        List<Target> targets,
        String title,
        String body,
        Map<String, String> data
) {
    public record Target(String userId, String deviceToken) {}
}
