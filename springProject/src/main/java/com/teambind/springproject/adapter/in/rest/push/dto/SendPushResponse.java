package com.teambind.springproject.adapter.in.rest.push.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 푸시 알림 발송 응답 DTO
 */
@Getter
@AllArgsConstructor
public class SendPushResponse {

    private Long notificationId;
    private String status;

    public static SendPushResponse success(Long notificationId) {
        return new SendPushResponse(notificationId, "ACCEPTED");
    }
}
