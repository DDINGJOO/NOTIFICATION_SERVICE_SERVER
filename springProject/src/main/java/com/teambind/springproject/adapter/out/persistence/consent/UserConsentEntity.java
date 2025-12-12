package com.teambind.springproject.adapter.out.persistence.consent;

import com.teambind.springproject.domain.model.consent.UserConsent;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 사용자 동의 정보 JPA Entity
 */
@Entity
@Table(name = "user_consent", indexes = {
        @Index(name = "idx_user_consent_user_id", columnList = "user_id", unique = true)
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class UserConsentEntity {

    @Id
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "service_consent", nullable = false)
    private boolean serviceConsent;

    @Column(name = "marketing_consent", nullable = false)
    private boolean marketingConsent;

    @Column(name = "night_ad_consent", nullable = false)
    private boolean nightAdConsent;

    @Column(name = "sms_consent", nullable = false)
    private boolean smsConsent;

    @Column(name = "email_consent", nullable = false)
    private boolean emailConsent;

    @Column(name = "kakao_consent", nullable = false)
    private boolean kakaoConsent;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    private UserConsentEntity(Long id, String userId, String phoneNumber,
                              boolean serviceConsent, boolean marketingConsent, boolean nightAdConsent,
                              boolean smsConsent, boolean emailConsent, boolean kakaoConsent) {
        this.id = id;
        this.userId = userId;
        this.phoneNumber = phoneNumber;
        this.serviceConsent = serviceConsent;
        this.marketingConsent = marketingConsent;
        this.nightAdConsent = nightAdConsent;
        this.smsConsent = smsConsent;
        this.emailConsent = emailConsent;
        this.kakaoConsent = kakaoConsent;
    }

    /**
     * 도메인 모델로부터 Entity 생성
     */
    public static UserConsentEntity fromDomain(UserConsent userConsent) {
        UserConsentEntity entity = new UserConsentEntity(
                userConsent.getId(),
                userConsent.getUserId(),
                userConsent.getPhoneNumber(),
                userConsent.getConsentSettings().isServiceConsent(),
                userConsent.getConsentSettings().isMarketingConsent(),
                userConsent.getConsentSettings().isNightAdConsent(),
                userConsent.getConsentSettings().isSmsConsent(),
                userConsent.getConsentSettings().isEmailConsent(),
                userConsent.getConsentSettings().isKakaoConsent()
        );
        entity.createdAt = userConsent.getCreatedAt();
        entity.updatedAt = userConsent.getUpdatedAt();
        return entity;
    }

    /**
     * 도메인 모델로 변환
     */
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
                createdAt,
                updatedAt
        );
    }

    /**
     * 동의 정보 업데이트
     */
    public void updateConsent(boolean serviceConsent, boolean marketingConsent, boolean nightAdConsent,
                              boolean smsConsent, boolean emailConsent, boolean kakaoConsent) {
        this.serviceConsent = serviceConsent;
        this.marketingConsent = marketingConsent;
        this.nightAdConsent = nightAdConsent;
        this.smsConsent = smsConsent;
        this.emailConsent = emailConsent;
        this.kakaoConsent = kakaoConsent;
    }

    /**
     * 전화번호 업데이트
     */
    public void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
