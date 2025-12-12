package com.teambind.springproject.domain.model.consent;

import java.time.LocalTime;
import lombok.Getter;

/**
 * 알림 수신 동의 설정 Value Object
 * 불변 객체로 Thread-safe 보장
 */
@Getter
public class ConsentSettings {

    private static final LocalTime NIGHT_START = LocalTime.of(21, 0);
    private static final LocalTime NIGHT_END = LocalTime.of(8, 0);

    private final boolean serviceConsent;
    private final boolean marketingConsent;
    private final boolean nightAdConsent;

    private ConsentSettings(boolean serviceConsent, boolean marketingConsent, boolean nightAdConsent) {
        this.serviceConsent = serviceConsent;
        this.marketingConsent = marketingConsent;
        this.nightAdConsent = nightAdConsent;
    }

    /**
     * 기본 동의 설정 생성
     * - 서비스 알림: true (기본 허용)
     * - 마케팅 알림: false
     * - 야간 광고: false
     */
    public static ConsentSettings createDefault() {
        return new ConsentSettings(true, false, false);
    }

    /**
     * 마케팅 동의 포함 설정 생성
     * - 서비스 알림: true
     * - 마케팅 알림: 지정값
     * - 야간 광고: false (명시적 동의 필요)
     */
    public static ConsentSettings withMarketingConsent(boolean marketingConsent) {
        return new ConsentSettings(true, marketingConsent, false);
    }

    /**
     * 전체 설정 생성
     */
    public static ConsentSettings of(boolean serviceConsent, boolean marketingConsent, boolean nightAdConsent) {
        return new ConsentSettings(serviceConsent, marketingConsent, nightAdConsent);
    }

    /**
     * 마케팅 동의 변경
     */
    public ConsentSettings changeMarketingConsent(boolean marketingConsent) {
        if (!marketingConsent) {
            return new ConsentSettings(this.serviceConsent, false, false);
        }
        return new ConsentSettings(this.serviceConsent, marketingConsent, this.nightAdConsent);
    }

    /**
     * 야간 광고 동의 변경
     */
    public ConsentSettings changeNightAdConsent(boolean nightAdConsent) {
        return new ConsentSettings(this.serviceConsent, this.marketingConsent, nightAdConsent);
    }

    /**
     * 서비스 알림 수신 가능 여부
     */
    public boolean canReceiveServiceNotification() {
        return serviceConsent;
    }

    /**
     * 광고성 알림 수신 가능 여부 (야간 시간대 고려)
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
                && nightAdConsent == that.nightAdConsent;
    }

    @Override
    public int hashCode() {
        int result = Boolean.hashCode(serviceConsent);
        result = 31 * result + Boolean.hashCode(marketingConsent);
        result = 31 * result + Boolean.hashCode(nightAdConsent);
        return result;
    }
}
