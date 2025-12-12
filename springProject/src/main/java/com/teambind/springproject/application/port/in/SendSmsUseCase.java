package com.teambind.springproject.application.port.in;

import com.teambind.springproject.domain.model.notification.NotificationType;
import java.util.List;

/**
 * SMS 발송 Inbound Port
 */
public interface SendSmsUseCase {

    /**
     * SMS 일괄 발송
     *
     * @param type 알림 타입 (TRANSACTIONAL/MARKETING)
     * @param targets 발송 대상 목록
     * @param content 메시지 내용
     * @return 발송 이력 ID 목록
     */
    List<Long> sendSms(NotificationType type, List<SmsTarget> targets, String content);

    /**
     * SMS 발송 대상
     */
    record SmsTarget(String userId, String phoneNumber) {}
}
