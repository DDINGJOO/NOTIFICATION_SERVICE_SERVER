package com.teambind.springproject.adapter.in.rest.sms.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * SMS 발송 응답 DTO
 */
@Getter
@AllArgsConstructor
public class SendSmsResponse {

    private List<Long> notificationIds;
    private int totalCount;
    private int successCount;
    private String status;

    public static SendSmsResponse success(List<Long> notificationIds) {
        return new SendSmsResponse(
                notificationIds,
                notificationIds.size(),
                notificationIds.size(),
                "ACCEPTED"
        );
    }
}
