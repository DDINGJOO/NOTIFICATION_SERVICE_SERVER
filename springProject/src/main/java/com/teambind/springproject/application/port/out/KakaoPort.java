package com.teambind.springproject.application.port.out;

import java.util.Map;

/**
 * 카카오 알림톡 발송 Outbound Port
 */
public interface KakaoPort {

    /**
     * 알림톡 발송
     *
     * @param phoneNumber 수신자 전화번호
     * @param templateCode 템플릿 코드
     * @param variables 템플릿 변수
     */
    void send(String phoneNumber, String templateCode, Map<String, String> variables);
}
