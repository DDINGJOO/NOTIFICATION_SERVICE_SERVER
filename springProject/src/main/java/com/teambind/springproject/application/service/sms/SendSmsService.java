package com.teambind.springproject.application.service.sms;

import com.teambind.springproject.application.port.in.SendSmsUseCase;
import com.teambind.springproject.application.port.out.NotificationHistoryPort;
import com.teambind.springproject.application.port.out.SmsPort;
import com.teambind.springproject.application.port.out.UserConsentCachePort;
import com.teambind.springproject.common.util.generator.PrimaryKeyGenerator;
import com.teambind.springproject.domain.model.consent.UserConsent;
import com.teambind.springproject.domain.model.notification.FailureReason;
import com.teambind.springproject.domain.model.notification.NotificationChannel;
import com.teambind.springproject.domain.model.notification.NotificationHistory;
import com.teambind.springproject.domain.model.notification.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * SMS 발송 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SendSmsService implements SendSmsUseCase {

    private final SmsPort smsPort;
    private final NotificationHistoryPort historyPort;
    private final UserConsentCachePort consentCachePort;
    private final PrimaryKeyGenerator keyGenerator;

    @Override
    @Transactional
    public Long sendSms(String userId, String phoneNumber, String content) {
        log.info("SMS 발송 요청. userId={}, phoneNumber={}", userId, phoneNumber);

        Long historyId = keyGenerator.generateLongKey();
        NotificationHistory history = NotificationHistory.create(
                historyId,
                userId,
                phoneNumber,
                NotificationChannel.SMS,
                NotificationType.TRANSACTIONAL,
                content
        );

        // 사용자 동의 확인 (마케팅 알림인 경우)
        if (history.isMarketing()) {
            boolean consentGranted = checkSmsConsent(userId);
            if (!consentGranted) {
                log.warn("SMS 동의 없음. userId={}", userId);
                history.markAsFailed(FailureReason.consentNotGranted("SMS 수신 동의가 없습니다."));
                historyPort.save(history);
                return historyId;
            }
        }

        try {
            smsPort.send(phoneNumber, content);
            history.markAsSuccess();
            log.info("SMS 발송 성공. historyId={}", historyId);
        } catch (Exception e) {
            log.error("SMS 발송 실패. historyId={}", historyId, e);
            history.markAsFailed(FailureReason.providerError(e.getMessage()));
        }

        historyPort.save(history);
        return historyId;
    }

    private boolean checkSmsConsent(String userId) {
        return consentCachePort.findByUserId(userId)
                .map(UserConsent::getConsentSettings)
                .map(settings -> settings.isMarketingConsent() && settings.isSmsConsent())
                .orElse(false);
    }
}
