package com.teambind.springproject.application.port.out;

import com.teambind.springproject.domain.model.consent.UserConsent;

import java.util.Optional;

/**
 * 사용자 동의 정보 Outbound Port
 */
public interface UserConsentPort {
	
	/**
	 * 동의 정보 저장
	 */
	UserConsent save(UserConsent userConsent);
	
	/**
	 * 사용자 ID로 동의 정보 조회
	 */
	Optional<UserConsent> findByUserId(String userId);
	
	/**
	 * 사용자 ID로 동의 정보 존재 여부 확인
	 */
	boolean existsByUserId(String userId);
}
