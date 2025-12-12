package com.teambind.springproject.adapter.in.rest.consent.dto;

import com.teambind.springproject.domain.model.consent.UserConsent;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

/**
 * 사용자 동의 정보 응답 DTO
 */
@Getter
@Builder
public class UserConsentResponse {

    private String userId;
    private boolean serviceConsent;
    private boolean marketingConsent;
    private boolean nightAdConsent;
    private LocalDateTime updatedAt;

    public static UserConsentResponse fromDomain(UserConsent userConsent) {
        return UserConsentResponse.builder()
                .userId(userConsent.getUserId())
                .serviceConsent(userConsent.getConsentSettings().isServiceConsent())
                .marketingConsent(userConsent.getConsentSettings().isMarketingConsent())
                .nightAdConsent(userConsent.getConsentSettings().isNightAdConsent())
                .updatedAt(userConsent.getUpdatedAt())
                .build();
    }
}
