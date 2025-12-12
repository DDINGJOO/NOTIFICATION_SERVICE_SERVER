package com.teambind.springproject.adapter.in.rest.kakao.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 카카오 알림톡 발송 응답 DTO
 */
@Getter
@AllArgsConstructor
public class SendKakaoResponse {

    private List<Long> notificationIds;
    private int totalCount;
    private int successCount;
    private String status;

    public static SendKakaoResponse success(List<Long> notificationIds) {
        return new SendKakaoResponse(
                notificationIds,
                notificationIds.size(),
                notificationIds.size(),
                "ACCEPTED"
        );
    }
}
