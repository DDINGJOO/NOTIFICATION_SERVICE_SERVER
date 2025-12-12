package com.teambind.springproject.application.port.out;

import java.util.Map;

/**
 * 푸시 알림 발송 Outbound Port
 */
public interface PushPort {

    /**
     * 단일 디바이스에 푸시 발송
     *
     * @param deviceToken FCM 디바이스 토큰
     * @param title 알림 제목
     * @param body 알림 본문
     */
    void send(String deviceToken, String title, String body);

    /**
     * 단일 디바이스에 데이터 포함 푸시 발송
     *
     * @param deviceToken FCM 디바이스 토큰
     * @param title 알림 제목
     * @param body 알림 본문
     * @param data 추가 데이터
     */
    void send(String deviceToken, String title, String body, Map<String, String> data);
}
