package com.teambind.springproject.domain.model.consent;

import java.time.LocalTime;
import lombok.Getter;

/**
 * 알림 수신 동의 설정 Value Object
 * 불변 객체로 Thread-safe 보장
 *
 * - serviceConsent: 서비스 알림 수신 동의
 * - marketingConsent: 마케팅 동의 (Auth Server에서 관리, 총괄)
 * - nightAdConsent: 야간 광고 수신 동의 (21:00~08:00)
 * - smsConsent: SMS 채널 수신 동의
 * - emailConsent: 이메일 채널 수신 동의
 * - kakaoConsent: 알림톡 채널 수신 동의
 */
@Getter
public class ConsentSettings {

    private static final LocalTime NIGHT_START = LocalTime.of(21, 0);
    private static final LocalTime NIGHT_END = LocalTime.of(8, 0);

    private final boolean serviceConsent;
    private final boolean marketingConsent;
    private final boolean nightAdConsent;
    private final boolean smsConsent;
    private final boolean emailConsent;
    private final boolean kakaoConsent;

    private ConsentSettings(boolean serviceConsent, boolean marketingConsent, boolean nightAdConsent,
                            boolean smsConsent, boolean emailConsent, boolean kakaoConsent) {
        this.serviceConsent = serviceConsent;
        this.marketingConsent = marketingConsent;
        this.nightAdConsent = nightAdConsent;
        this.smsConsent = smsConsent;
        this.emailConsent = emailConsent;
        this.kakaoConsent = kakaoConsent;
    }

    /**
     * 기본 동의 설정 생성
     * - 서비스 알림: true (기본 허용)
     * - 마케팅 알림: false
     * - 야간 광고: false
     * - 채널별 동의: 모두 true (마케팅 동의 시 기본 허용)
     */
    public static ConsentSettings createDefault() {
        return new ConsentSettings(true, false, false, true, true, true);
    }

    /**
     * 마케팅 동의 포함 설정 생성
     * - 서비스 알림: true
     * - 마케팅 알림: 지정값
     * - 야간 광고: false (명시적 동의 필요)
     * - 채널별 동의: 모두 true (기본 허용)
     */
    public static ConsentSettings withMarketingConsent(boolean marketingConsent) {
        return new ConsentSettings(true, marketingConsent, false, true, true, true);
    }

    /**
     * 전체 설정 생성 (기존 호환)
     */
    public static ConsentSettings of(boolean serviceConsent, boolean marketingConsent, boolean nightAdConsent) {
        return new ConsentSettings(serviceConsent, marketingConsent, nightAdConsent, true, true, true);
    }

    /**
     * 전체 설정 생성 (채널별 동의 포함)
     */
    public static ConsentSettings of(boolean serviceConsent, boolean marketingConsent, boolean nightAdConsent,
                                     boolean smsConsent, boolean emailConsent, boolean kakaoConsent) {
        return new ConsentSettings(serviceConsent, marketingConsent, nightAdConsent,
                smsConsent, emailConsent, kakaoConsent);
    }

    /**
     * 마케팅 동의 변경
     * 마케팅 동의 철회 시 야간 광고 동의도 철회
     */
    public ConsentSettings changeMarketingConsent(boolean marketingConsent) {
        if (!marketingConsent) {
            return new ConsentSettings(this.serviceConsent, false, false,
                    this.smsConsent, this.emailConsent, this.kakaoConsent);
        }
        return new ConsentSettings(this.serviceConsent, marketingConsent, this.nightAdConsent,
                this.smsConsent, this.emailConsent, this.kakaoConsent);
    }

    /**
     * 야간 광고 동의 변경
     */
    public ConsentSettings changeNightAdConsent(boolean nightAdConsent) {
        return new ConsentSettings(this.serviceConsent, this.marketingConsent, nightAdConsent,
                this.smsConsent, this.emailConsent, this.kakaoConsent);
    }

    /**
     * SMS 채널 동의 변경
     */
    public ConsentSettings changeSmsConsent(boolean smsConsent) {
        return new ConsentSettings(this.serviceConsent, this.marketingConsent, this.nightAdConsent,
                smsConsent, this.emailConsent, this.kakaoConsent);
    }

    /**
     * 이메일 채널 동의 변경
     */
    public ConsentSettings changeEmailConsent(boolean emailConsent) {
        return new ConsentSettings(this.serviceConsent, this.marketingConsent, this.nightAdConsent,
                this.smsConsent, emailConsent, this.kakaoConsent);
    }

    /**
     * 알림톡 채널 동의 변경
     */
    public ConsentSettings changeKakaoConsent(boolean kakaoConsent) {
        return new ConsentSettings(this.serviceConsent, this.marketingConsent, this.nightAdConsent,
                this.smsConsent, this.emailConsent, kakaoConsent);
    }

    /**
     * 서비스 알림 수신 가능 여부
     */
    public boolean canReceiveServiceNotification() {
        return serviceConsent;
    }

    /**
     * 광고성 알림 수신 가능 여부 (야간 시간대 고려, 채널 무관)
     */
    public boolean canReceiveAdNotification(LocalTime currentTime) {
        if (!marketingConsent) {
            return false;
        }
        if (isNightTime(currentTime) && !nightAdConsent) {
            return false;
        }
        return true;
    }

    /**
     * SMS 광고 수신 가능 여부
     */
    public boolean canReceiveSmsAd(LocalTime currentTime) {
        return canReceiveAdNotification(currentTime) && smsConsent;
    }

    /**
     * 이메일 광고 수신 가능 여부
     */
    public boolean canReceiveEmailAd(LocalTime currentTime) {
        return canReceiveAdNotification(currentTime) && emailConsent;
    }

    /**
     * 알림톡 광고 수신 가능 여부
     */
    public boolean canReceiveKakaoAd(LocalTime currentTime) {
        return canReceiveAdNotification(currentTime) && kakaoConsent;
    }

    /**
     * 야간 시간대 여부 판단 (21:00 ~ 08:00)
     */
    private boolean isNightTime(LocalTime time) {
        return time.isAfter(NIGHT_START) || time.isBefore(NIGHT_END);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConsentSettings that = (ConsentSettings) o;
        return serviceConsent == that.serviceConsent
                && marketingConsent == that.marketingConsent
                && nightAdConsent == that.nightAdConsent
                && smsConsent == that.smsConsent
                && emailConsent == that.emailConsent
                && kakaoConsent == that.kakaoConsent;
    }

    @Override
    public int hashCode() {
        int result = Boolean.hashCode(serviceConsent);
        result = 31 * result + Boolean.hashCode(marketingConsent);
        result = 31 * result + Boolean.hashCode(nightAdConsent);
        result = 31 * result + Boolean.hashCode(smsConsent);
        result = 31 * result + Boolean.hashCode(emailConsent);
        result = 31 * result + Boolean.hashCode(kakaoConsent);
        return result;
    }
}
