package com.teambind.springproject.domain.event;

import com.teambind.springproject.domain.model.notification.NotificationType;
import java.util.List;

/**
 * SMS 발송 이벤트
 */
public record SmsSendEvent(
        NotificationType type,
        List<Target> targets,
        String content
) {
    public record Target(String userId, String phoneNumber) {}
}
