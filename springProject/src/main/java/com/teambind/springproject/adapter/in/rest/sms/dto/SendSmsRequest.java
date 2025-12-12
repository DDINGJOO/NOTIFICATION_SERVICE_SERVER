package com.teambind.springproject.adapter.in.rest.sms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * SMS 발송 요청 DTO
 */
@Getter
@NoArgsConstructor
public class SendSmsRequest {

    @NotBlank(message = "사용자 ID는 필수입니다")
    private String userId;

    @NotBlank(message = "전화번호는 필수입니다")
    @Pattern(regexp = "^01[0-9]{8,9}$", message = "올바른 전화번호 형식이 아닙니다")
    private String phoneNumber;

    @NotBlank(message = "메시지 내용은 필수입니다")
    private String content;
}
