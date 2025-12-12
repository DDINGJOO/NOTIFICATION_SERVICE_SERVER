package com.teambind.springproject.adapter.in.rest.push.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 푸시 알림 발송 응답 DTO
 */
@Getter
@AllArgsConstructor
public class SendPushResponse {

    private List<Long> notificationIds;
    private int totalCount;
    private int successCount;
    private String status;

    public static SendPushResponse success(List<Long> notificationIds) {
        return new SendPushResponse(
                notificationIds,
                notificationIds.size(),
                notificationIds.size(),
                "ACCEPTED"
        );
    }
}
