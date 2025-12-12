package com.teambind.springproject.application.service.email;

import com.teambind.springproject.application.port.in.SendEmailUseCase;
import com.teambind.springproject.application.port.out.EmailPort;
import com.teambind.springproject.application.port.out.NotificationHistoryPort;
import com.teambind.springproject.application.port.out.UserConsentCachePort;
import com.teambind.springproject.common.util.generator.PrimaryKeyGenerator;
import com.teambind.springproject.domain.model.consent.UserConsent;
import com.teambind.springproject.domain.model.notification.FailureReason;
import com.teambind.springproject.domain.model.notification.NotificationChannel;
import com.teambind.springproject.domain.model.notification.NotificationHistory;
import com.teambind.springproject.domain.model.notification.NotificationType;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 이메일 발송 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SendEmailService implements SendEmailUseCase {

    private final EmailPort emailPort;
    private final NotificationHistoryPort historyPort;
    private final UserConsentCachePort consentCachePort;
    private final PrimaryKeyGenerator keyGenerator;

    @Override
    @Transactional
    public List<Long> sendEmail(NotificationType type, List<EmailTarget> targets, String title, String content) {
        log.info("이메일 일괄 발송 요청. type={}, targetCount={}", type, targets.size());

        List<Long> historyIds = new ArrayList<>();

        for (EmailTarget target : targets) {
            Long historyId = sendSingleEmail(type, target, title, content);
            historyIds.add(historyId);
        }

        log.info("이메일 일괄 발송 완료. totalCount={}", historyIds.size());
        return historyIds;
    }

    private Long sendSingleEmail(NotificationType type, EmailTarget target, String title, String content) {
        Long historyId = keyGenerator.generateLongKey();
        String fullContent = String.format("[%s] %s", title, content);

        NotificationHistory history = NotificationHistory.create(
                historyId,
                target.userId(),
                target.email(),
                NotificationChannel.EMAIL,
                type,
                fullContent
        );

        // 마케팅 알림인 경우 동의 확인
        if (type == NotificationType.MARKETING) {
            boolean consentGranted = checkEmailConsent(target.userId());
            if (!consentGranted) {
                log.warn("이메일 동의 없음. userId={}", target.userId());
                history.markAsFailed(FailureReason.consentNotGranted("이메일 수신 동의가 없습니다."));
                historyPort.save(history);
                return historyId;
            }
        }

        try {
            emailPort.send(target.email(), title, content);
            history.markAsSuccess();
            log.debug("이메일 발송 성공. historyId={}, userId={}", historyId, target.userId());
        } catch (Exception e) {
            log.error("이메일 발송 실패. historyId={}, userId={}", historyId, target.userId(), e);
            history.markAsFailed(FailureReason.providerError(e.getMessage()));
        }

        historyPort.save(history);
        return historyId;
    }

    private boolean checkEmailConsent(String userId) {
        return consentCachePort.findByUserId(userId)
                .map(UserConsent::getConsentSettings)
                .map(settings -> settings.isMarketingConsent() && settings.isEmailConsent())
                .orElse(false);
    }
}
