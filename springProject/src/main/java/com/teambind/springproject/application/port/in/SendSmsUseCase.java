package com.teambind.springproject.application.port.in;

/**
 * SMS 발송 Inbound Port
 */
public interface SendSmsUseCase {

    /**
     * SMS 발송
     *
     * @param userId 사용자 ID
     * @param phoneNumber 수신자 전화번호
     * @param content 메시지 내용
     * @return 발송 이력 ID
     */
    Long sendSms(String userId, String phoneNumber, String content);
}
