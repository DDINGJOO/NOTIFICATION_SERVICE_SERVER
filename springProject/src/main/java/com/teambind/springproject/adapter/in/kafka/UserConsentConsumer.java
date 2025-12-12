package com.teambind.springproject.adapter.in.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teambind.springproject.application.port.in.UserConsentUseCase;
import com.teambind.springproject.domain.event.UserConsentChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 사용자 동의 변경 Kafka Consumer
 * Topic: user-consent-changed
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserConsentConsumer {
	
	private static final String MARKETING_CONSENT_ID = "MARKETING_CONSENT";
	
	private final UserConsentUseCase userConsentUseCase;
	private final ObjectMapper objectMapper;
	
	@KafkaListener(
			topics = "user-consent-changed",
			groupId = "notification-consumer-group"
	)
	public void consume(String message) {
		log.debug("Received user-consent-changed: {}", message);
		
		try {
			UserConsentChangedEvent event = objectMapper.readValue(message, UserConsentChangedEvent.class);
			
			if (MARKETING_CONSENT_ID.equals(event.getConsentId())) {
				userConsentUseCase.handleMarketingConsentChanged(event.getUserId(), event.isConsented());
				log.info("Marketing consent processed: userId={}, consented={}",
						event.getUserId(), event.isConsented());
			} else {
				log.debug("Ignoring non-marketing consent: consentId={}", event.getConsentId());
			}
			
		} catch (Exception e) {
			log.error("Failed to process user-consent-changed: {}", message, e);
			throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
		}
	}
}
