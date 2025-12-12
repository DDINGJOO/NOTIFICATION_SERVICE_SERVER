package com.teambind.springproject.adapter.in.rest.sms.dto;

import com.teambind.springproject.domain.model.notification.NotificationType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * SMS 발송 요청 DTO
 */
@Getter
@NoArgsConstructor
public class SendSmsRequest {

    @NotNull(message = "알림 타입은 필수입니다")
    private NotificationType type;

    @NotEmpty(message = "발송 대상은 최소 1명 이상이어야 합니다")
    @Valid
    private List<SmsTarget> targets;

    @NotBlank(message = "메시지 내용은 필수입니다")
    private String content;
}
