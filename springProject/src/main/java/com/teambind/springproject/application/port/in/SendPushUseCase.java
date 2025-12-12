package com.teambind.springproject.application.port.in;

import java.util.Map;

/**
 * 푸시 알림 발송 Inbound Port
 */
public interface SendPushUseCase {

    /**
     * 푸시 알림 발송
     *
     * @param userId 사용자 ID
     * @param deviceToken FCM 디바이스 토큰
     * @param title 알림 제목
     * @param body 알림 본문
     * @return 발송 이력 ID
     */
    Long sendPush(String userId, String deviceToken, String title, String body);

    /**
     * 데이터 포함 푸시 알림 발송
     *
     * @param userId 사용자 ID
     * @param deviceToken FCM 디바이스 토큰
     * @param title 알림 제목
     * @param body 알림 본문
     * @param data 추가 데이터
     * @return 발송 이력 ID
     */
    Long sendPush(String userId, String deviceToken, String title, String body, Map<String, String> data);
}
