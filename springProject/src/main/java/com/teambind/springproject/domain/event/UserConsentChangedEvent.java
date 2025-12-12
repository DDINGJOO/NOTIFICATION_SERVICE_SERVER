package com.teambind.springproject.domain.event;

import lombok.*;

/**
 * Auth Server에서 발행하는 동의 변경 이벤트
 * Topic: user-consent-changed
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserConsentChangedEvent {
	
	private String userId;
	private String consentId;
	private boolean consented;
	private String changedAt;
}
