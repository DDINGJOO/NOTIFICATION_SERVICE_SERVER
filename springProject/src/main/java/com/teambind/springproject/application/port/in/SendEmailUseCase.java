package com.teambind.springproject.application.port.in;

import com.teambind.springproject.domain.model.notification.NotificationType;
import java.util.List;

/**
 * 이메일 발송 Inbound Port
 */
public interface SendEmailUseCase {

    /**
     * 이메일 일괄 발송
     *
     * @param type 알림 타입 (TRANSACTIONAL/MARKETING)
     * @param targets 발송 대상 목록
     * @param title 이메일 제목
     * @param content 이메일 내용 (HTML)
     * @return 발송 이력 ID 목록
     */
    List<Long> sendEmail(NotificationType type, List<EmailTarget> targets, String title, String content);

    /**
     * 이메일 발송 대상
     */
    record EmailTarget(String userId, String email) {}
}
