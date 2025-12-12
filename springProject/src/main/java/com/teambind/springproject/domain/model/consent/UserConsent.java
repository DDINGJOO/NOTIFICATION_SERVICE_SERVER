package com.teambind.springproject.domain.model.consent;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import lombok.Getter;

/**
 * 사용자 알림 수신 동의 Aggregate Root
 */
@Getter
public class UserConsent {

    private static final ZoneId KOREA_ZONE = ZoneId.of("Asia/Seoul");

    private final Long id;
    private final String userId;
    private String phoneNumber;
    private ConsentSettings consentSettings;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private UserConsent(Long id, String userId, String phoneNumber, ConsentSettings consentSettings,
                        LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.phoneNumber = phoneNumber;
        this.consentSettings = consentSettings;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * 신규 사용자 동의 생성 (기본 설정)
     */
    public static UserConsent createNew(Long id, String userId) {
        LocalDateTime now = LocalDateTime.now();
        return new UserConsent(id, userId, null, ConsentSettings.createDefault(), now, now);
    }

    /**
     * 마케팅 동의 포함하여 생성
     */
    public static UserConsent createWithMarketingConsent(Long id, String userId, boolean marketingConsent) {
        LocalDateTime now = LocalDateTime.now();
        return new UserConsent(id, userId, null, ConsentSettings.withMarketingConsent(marketingConsent), now, now);
    }

    /**
     * DB 조회 결과로부터 복원 (기존 호환)
     */
    public static UserConsent restore(Long id, String userId, boolean serviceConsent,
                                      boolean marketingConsent, boolean nightAdConsent,
                                      LocalDateTime createdAt, LocalDateTime updatedAt) {
        ConsentSettings settings = ConsentSettings.of(serviceConsent, marketingConsent, nightAdConsent);
        return new UserConsent(id, userId, null, settings, createdAt, updatedAt);
    }

    /**
     * DB 조회 결과로부터 복원 (채널별 동의 포함)
     */
    public static UserConsent restore(Long id, String userId, String phoneNumber,
                                      boolean serviceConsent, boolean marketingConsent, boolean nightAdConsent,
                                      boolean smsConsent, boolean emailConsent, boolean kakaoConsent,
                                      LocalDateTime createdAt, LocalDateTime updatedAt) {
        ConsentSettings settings = ConsentSettings.of(serviceConsent, marketingConsent, nightAdConsent,
                smsConsent, emailConsent, kakaoConsent);
        return new UserConsent(id, userId, phoneNumber, settings, createdAt, updatedAt);
    }

    /**
     * 전화번호 변경
     */
    public void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 마케팅 동의 변경
     */
    public void changeMarketingConsent(boolean marketingConsent) {
        this.consentSettings = this.consentSettings.changeMarketingConsent(marketingConsent);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 야간 광고 동의 변경
     */
    public void changeNightAdConsent(boolean nightAdConsent) {
        this.consentSettings = this.consentSettings.changeNightAdConsent(nightAdConsent);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * SMS 채널 동의 변경
     */
    public void changeSmsConsent(boolean smsConsent) {
        this.consentSettings = this.consentSettings.changeSmsConsent(smsConsent);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 이메일 채널 동의 변경
     */
    public void changeEmailConsent(boolean emailConsent) {
        this.consentSettings = this.consentSettings.changeEmailConsent(emailConsent);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 알림톡 채널 동의 변경
     */
    public void changeKakaoConsent(boolean kakaoConsent) {
        this.consentSettings = this.consentSettings.changeKakaoConsent(kakaoConsent);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 서비스 알림 수신 가능 여부
     */
    public boolean canReceiveServiceNotification() {
        return consentSettings.canReceiveServiceNotification();
    }

    /**
     * 광고성 알림 수신 가능 여부
     */
    public boolean canReceiveAdNotification() {
        return consentSettings.canReceiveAdNotification(LocalTime.now(KOREA_ZONE));
    }

    /**
     * SMS 광고 수신 가능 여부
     */
    public boolean canReceiveSmsAd() {
        return consentSettings.canReceiveSmsAd(LocalTime.now(KOREA_ZONE));
    }

    /**
     * 이메일 광고 수신 가능 여부
     */
    public boolean canReceiveEmailAd() {
        return consentSettings.canReceiveEmailAd(LocalTime.now(KOREA_ZONE));
    }

    /**
     * 알림톡 광고 수신 가능 여부
     */
    public boolean canReceiveKakaoAd() {
        return consentSettings.canReceiveKakaoAd(LocalTime.now(KOREA_ZONE));
    }
}
