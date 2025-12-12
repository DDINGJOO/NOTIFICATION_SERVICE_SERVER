package com.teambind.springproject.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Auth Server에서 발행하는 전화번호 인증 완료 이벤트
 * Topic: phone-number-verified
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PhoneNumberVerifiedEvent {

    private String userId;
    private String phoneNumber;
}
