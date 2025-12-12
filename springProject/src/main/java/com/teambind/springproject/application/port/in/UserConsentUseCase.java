package com.teambind.springproject.application.port.in;

import com.teambind.springproject.domain.model.consent.UserConsent;
import java.util.Optional;

/**
 * 사용자 동의 정보 관리 UseCase (Inbound Port)
 */
public interface UserConsentUseCase {

    /**
     * 마케팅 동의 변경 처리 (Kafka 이벤트 수신 시)
     */
    void handleMarketingConsentChanged(String userId, boolean consented);

    /**
     * 야간 광고 동의 변경
     */
    void changeNightAdConsent(String userId, boolean consented);

    /**
     * 사용자 동의 정보 조회
     */
    Optional<UserConsent> getUserConsent(String userId);

    /**
     * 광고성 알림 수신 가능 여부 확인
     */
    boolean canReceiveAdNotification(String userId);

    /**
     * 서비스 알림 수신 가능 여부 확인
     */
    boolean canReceiveServiceNotification(String userId);
}
