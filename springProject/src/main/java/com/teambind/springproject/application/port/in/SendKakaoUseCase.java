package com.teambind.springproject.application.port.in;

import com.teambind.springproject.domain.model.notification.NotificationType;
import java.util.List;
import java.util.Map;

/**
 * 카카오 알림톡 발송 Inbound Port
 */
public interface SendKakaoUseCase {

    /**
     * 알림톡 일괄 발송
     *
     * @param type 알림 타입 (TRANSACTIONAL/MARKETING)
     * @param targets 발송 대상 목록
     * @param templateCode 템플릿 코드
     * @param variables 템플릿 변수
     * @return 발송 이력 ID 목록
     */
    List<Long> sendKakao(NotificationType type, List<KakaoTarget> targets, String templateCode, Map<String, String> variables);

    /**
     * 카카오 발송 대상
     */
    record KakaoTarget(String userId, String phoneNumber) {}
}
