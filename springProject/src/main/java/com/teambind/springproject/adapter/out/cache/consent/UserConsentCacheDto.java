package com.teambind.springproject.adapter.out.cache.consent;

import com.teambind.springproject.domain.model.consent.UserConsent;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Redis 캐시용 UserConsent DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserConsentCacheDto {

    private Long id;
    private String userId;
    private String phoneNumber;
    private boolean serviceConsent;
    private boolean marketingConsent;
    private boolean nightAdConsent;
    private boolean smsConsent;
    private boolean emailConsent;
    private boolean kakaoConsent;
    private String createdAt;
    private String updatedAt;

    public static UserConsentCacheDto fromDomain(UserConsent userConsent) {
        return UserConsentCacheDto.builder()
                .id(userConsent.getId())
                .userId(userConsent.getUserId())
                .phoneNumber(userConsent.getPhoneNumber())
                .serviceConsent(userConsent.getConsentSettings().isServiceConsent())
                .marketingConsent(userConsent.getConsentSettings().isMarketingConsent())
                .nightAdConsent(userConsent.getConsentSettings().isNightAdConsent())
                .smsConsent(userConsent.getConsentSettings().isSmsConsent())
                .emailConsent(userConsent.getConsentSettings().isEmailConsent())
                .kakaoConsent(userConsent.getConsentSettings().isKakaoConsent())
                .createdAt(userConsent.getCreatedAt().toString())
                .updatedAt(userConsent.getUpdatedAt().toString())
                .build();
    }

    public UserConsent toDomain() {
        return UserConsent.restore(
                id,
                userId,
                phoneNumber,
                serviceConsent,
                marketingConsent,
                nightAdConsent,
                smsConsent,
                emailConsent,
                kakaoConsent,
                LocalDateTime.parse(createdAt),
                LocalDateTime.parse(updatedAt)
        );
    }
}
