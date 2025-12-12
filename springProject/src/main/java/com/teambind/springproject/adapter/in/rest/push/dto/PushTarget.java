package com.teambind.springproject.adapter.in.rest.push.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 푸시 발송 대상 DTO
 */
@Getter
@NoArgsConstructor
public class PushTarget {

    @NotBlank(message = "사용자 ID는 필수입니다")
    private String userId;

    @NotBlank(message = "디바이스 토큰은 필수입니다")
    private String deviceToken;

    public PushTarget(String userId, String deviceToken) {
        this.userId = userId;
        this.deviceToken = deviceToken;
    }
}
