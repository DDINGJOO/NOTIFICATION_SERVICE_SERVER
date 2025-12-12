package com.teambind.springproject.application.port.out;

/**
 * SMS 발송 Outbound Port
 */
public interface SmsPort {

    /**
     * SMS 발송
     *
     * @param to 수신자 전화번호
     * @param content 메시지 내용
     */
    void send(String to, String content);

    /**
     * 인증 코드 SMS 발송
     *
     * @param to 수신자 전화번호
     * @param code 인증 코드
     */
    void sendVerificationSms(String to, String code);
}
