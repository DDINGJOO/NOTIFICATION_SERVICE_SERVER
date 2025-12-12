package com.teambind.springproject.adapter.in.rest.sms.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * SMS 발송 응답 DTO
 */
@Getter
@AllArgsConstructor
public class SendSmsResponse {

    private Long notificationId;
    private String status;

    public static SendSmsResponse success(Long notificationId) {
        return new SendSmsResponse(notificationId, "ACCEPTED");
    }
}
