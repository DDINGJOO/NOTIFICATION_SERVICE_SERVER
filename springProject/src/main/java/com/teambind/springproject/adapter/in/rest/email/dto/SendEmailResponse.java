package com.teambind.springproject.adapter.in.rest.email.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 이메일 발송 응답 DTO
 */
@Getter
@AllArgsConstructor
public class SendEmailResponse {

    private List<Long> notificationIds;
    private int totalCount;
    private int successCount;
    private String status;

    public static SendEmailResponse success(List<Long> notificationIds) {
        return new SendEmailResponse(
                notificationIds,
                notificationIds.size(),
                notificationIds.size(),
                "ACCEPTED"
        );
    }
}
