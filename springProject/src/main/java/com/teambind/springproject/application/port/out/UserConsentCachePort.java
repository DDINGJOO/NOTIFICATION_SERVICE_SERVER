package com.teambind.springproject.application.port.out;

import com.teambind.springproject.domain.model.consent.UserConsent;
import java.util.Optional;

/**
 * 사용자 동의 정보 캐시 Outbound Port
 */
public interface UserConsentCachePort {

    /**
     * 캐시에 동의 정보 저장
     */
    void save(UserConsent userConsent);

    /**
     * 캐시에서 동의 정보 조회
     */
    Optional<UserConsent> findByUserId(String userId);

    /**
     * 캐시에서 동의 정보 삭제
     */
    void evict(String userId);
}
