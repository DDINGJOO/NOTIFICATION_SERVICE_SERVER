package com.teambind.springproject.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Auth Server에서 발행하는 SMS 인증 요청 이벤트
 * Topic: sms-confirm-request
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SmsConfirmEvent {

    private String phoneNumber;
    private String code;
}
