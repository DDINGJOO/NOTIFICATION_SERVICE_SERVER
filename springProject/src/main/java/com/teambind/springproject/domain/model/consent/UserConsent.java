package com.teambind.springproject.domain.model.consent;

import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 사용자 알림 수신 동의 Aggregate Root
 */
@Getter
public class UserConsent {
	
	private final Long id;
	private final String userId;
	private final LocalDateTime createdAt;
	private ConsentSettings consentSettings;
	private LocalDateTime updatedAt;
	
	private UserConsent(Long id, String userId, ConsentSettings consentSettings,
	                    LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.id = id;
		this.userId = userId;
		this.consentSettings = consentSettings;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
	
	/**
	 * 신규 사용자 동의 생성 (기본 설정)
	 */
	public static UserConsent createNew(Long id, String userId) {
		LocalDateTime now = LocalDateTime.now();
		return new UserConsent(id, userId, ConsentSettings.createDefault(), now, now);
	}
	
	/**
	 * 마케팅 동의 포함하여 생성
	 */
	public static UserConsent createWithMarketingConsent(Long id, String userId, boolean marketingConsent) {
		LocalDateTime now = LocalDateTime.now();
		return new UserConsent(id, userId, ConsentSettings.withMarketingConsent(marketingConsent), now, now);
	}
	
	/**
	 * DB 조회 결과로부터 복원
	 */
	public static UserConsent restore(Long id, String userId, boolean serviceConsent,
	                                  boolean marketingConsent, boolean nightAdConsent,
	                                  LocalDateTime createdAt, LocalDateTime updatedAt) {
		ConsentSettings settings = ConsentSettings.of(serviceConsent, marketingConsent, nightAdConsent);
		return new UserConsent(id, userId, settings, createdAt, updatedAt);
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
	 * 서비스 알림 수신 가능 여부
	 */
	public boolean canReceiveServiceNotification() {
		return consentSettings.canReceiveServiceNotification();
	}
	
	/**
	 * 광고성 알림 수신 가능 여부
	 */
	public boolean canReceiveAdNotification() {
		return consentSettings.canReceiveAdNotification(java.time.LocalTime.now(
				java.time.ZoneId.of("Asia/Seoul")));
	}
}
