package com.teambind.springproject.domain.event;

import com.teambind.springproject.domain.model.notification.NotificationType;
import java.util.List;
import java.util.Map;

/**
 * 카카오 알림톡 발송 이벤트
 */
public record KakaoSendEvent(
        NotificationType type,
        List<Target> targets,
        String templateCode,
        Map<String, String> variables
) {
    public record Target(String userId, String phoneNumber) {}
}
