package com.teambind.springproject.application.service.consent;

import com.teambind.springproject.application.port.in.UserConsentUseCase;
import com.teambind.springproject.application.port.out.UserConsentCachePort;
import com.teambind.springproject.application.port.out.UserConsentPort;
import com.teambind.springproject.common.util.generator.PrimaryKeyGenerator;
import com.teambind.springproject.domain.model.consent.UserConsent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 사용자 동의 정보 관리 서비스
 * Cache-Aside 패턴 적용: 캐시 우선 조회, 미스 시 DB 조회 후 캐싱
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserConsentService implements UserConsentUseCase {
	
	private final UserConsentPort userConsentPort;
	private final UserConsentCachePort userConsentCachePort;
	private final PrimaryKeyGenerator keyGenerator;
	
	@Override
	public void handleMarketingConsentChanged(String userId, boolean consented) {
		log.debug("Processing marketing consent change: userId={}, consented={}", userId, consented);
		
		UserConsent userConsent = userConsentPort.findByUserId(userId)
				.map(existing -> {
					existing.changeMarketingConsent(consented);
					return existing;
				})
				.orElseGet(() -> UserConsent.createWithMarketingConsent(
						keyGenerator.generateLongKey(), userId, consented));
		
		UserConsent saved = userConsentPort.save(userConsent);
		userConsentCachePort.save(saved);
		log.info("Marketing consent updated: userId={}, consented={}", userId, consented);
	}
	
	@Override
	public void changeNightAdConsent(String userId, boolean consented) {
		log.debug("Processing night ad consent change: userId={}, consented={}", userId, consented);
		
		UserConsent userConsent = findByUserIdWithCache(userId)
				.orElseThrow(() -> new IllegalArgumentException("User consent not found: " + userId));
		
		userConsent.changeNightAdConsent(consented);
		UserConsent saved = userConsentPort.save(userConsent);
		userConsentCachePort.save(saved);
		log.info("Night ad consent updated: userId={}, consented={}", userId, consented);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Optional<UserConsent> getUserConsent(String userId) {
		return findByUserIdWithCache(userId);
	}
	
	@Override
	@Transactional(readOnly = true)
	public boolean canReceiveAdNotification(String userId) {
		return findByUserIdWithCache(userId)
				.map(UserConsent::canReceiveAdNotification)
				.orElse(false);
	}
	
	@Override
	@Transactional(readOnly = true)
	public boolean canReceiveServiceNotification(String userId) {
		return findByUserIdWithCache(userId)
				.map(UserConsent::canReceiveServiceNotification)
				.orElse(true);
	}
	
	/**
	 * Cache-Aside 패턴: 캐시 우선 조회, 미스 시 DB 조회 후 캐싱
	 */
	private Optional<UserConsent> findByUserIdWithCache(String userId) {
		Optional<UserConsent> cached = userConsentCachePort.findByUserId(userId);
		if (cached.isPresent()) {
			return cached;
		}
		
		Optional<UserConsent> fromDb = userConsentPort.findByUserId(userId);
		fromDb.ifPresent(userConsentCachePort::save);
		return fromDb;
	}
}
