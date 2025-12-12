package com.teambind.springproject.domain.event;

import com.teambind.springproject.domain.model.notification.NotificationType;
import java.util.List;

/**
 * 이메일 발송 이벤트
 */
public record EmailSendEvent(
        NotificationType type,
        List<Target> targets,
        String title,
        String content
) {
    public record Target(String userId, String email) {}
}
