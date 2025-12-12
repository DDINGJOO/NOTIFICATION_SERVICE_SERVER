package com.teambind.springproject.adapter.in.rest.push.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 푸시 알림 발송 요청 DTO
 */
@Getter
@NoArgsConstructor
public class SendPushRequest {

    @NotBlank(message = "사용자 ID는 필수입니다")
    private String userId;

    @NotBlank(message = "디바이스 토큰은 필수입니다")
    private String deviceToken;

    @NotBlank(message = "제목은 필수입니다")
    private String title;

    @NotBlank(message = "본문은 필수입니다")
    private String body;

    private Map<String, String> data;
}
