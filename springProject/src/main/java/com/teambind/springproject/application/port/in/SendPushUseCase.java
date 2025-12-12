package com.teambind.springproject.application.port.in;

import com.teambind.springproject.domain.model.notification.NotificationType;
import java.util.List;
import java.util.Map;

/**
 * 푸시 알림 발송 Inbound Port
 */
public interface SendPushUseCase {

    /**
     * 푸시 알림 일괄 발송
     *
     * @param type 알림 타입 (TRANSACTIONAL/MARKETING)
     * @param targets 발송 대상 목록
     * @param title 알림 제목
     * @param body 알림 본문
     * @param data 추가 데이터 (nullable)
     * @return 발송 이력 ID 목록
     */
    List<Long> sendPush(NotificationType type, List<PushTarget> targets, String title, String body, Map<String, String> data);

    /**
     * 푸시 발송 대상
     */
    record PushTarget(String userId, String deviceToken) {}
}
