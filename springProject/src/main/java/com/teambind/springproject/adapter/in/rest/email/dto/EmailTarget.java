package com.teambind.springproject.adapter.in.rest.email.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 이메일 발송 대상 DTO
 */
@Getter
@NoArgsConstructor
public class EmailTarget {

    @NotBlank(message = "사용자 ID는 필수입니다")
    private String userId;

    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;

    public EmailTarget(String userId, String email) {
        this.userId = userId;
        this.email = email;
    }
}
