package com.teambind.springproject.application.service.kakao;

import com.teambind.springproject.application.port.in.SendKakaoUseCase;
import com.teambind.springproject.application.port.out.KakaoPort;
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
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 카카오 알림톡 발송 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SendKakaoService implements SendKakaoUseCase {

    private final KakaoPort kakaoPort;
    private final NotificationHistoryPort historyPort;
    private final UserConsentCachePort consentCachePort;
    private final PrimaryKeyGenerator keyGenerator;

    @Override
    @Transactional
    public List<Long> sendKakao(NotificationType type, List<KakaoTarget> targets, String templateCode, Map<String, String> variables) {
        log.info("알림톡 일괄 발송 요청. type={}, targetCount={}, templateCode={}", type, targets.size(), templateCode);

        List<Long> historyIds = new ArrayList<>();

        for (KakaoTarget target : targets) {
            Long historyId = sendSingleKakao(type, target, templateCode, variables);
            historyIds.add(historyId);
        }

        log.info("알림톡 일괄 발송 완료. totalCount={}", historyIds.size());
        return historyIds;
    }

    private Long sendSingleKakao(NotificationType type, KakaoTarget target, String templateCode, Map<String, String> variables) {
        Long historyId = keyGenerator.generateLongKey();
        String content = String.format("[템플릿:%s] %s", templateCode, variables);

        NotificationHistory history = NotificationHistory.create(
                historyId,
                target.userId(),
                target.phoneNumber(),
                NotificationChannel.KAKAO,
                type,
                content
        );

        // 마케팅 알림인 경우 동의 확인
        if (type == NotificationType.MARKETING) {
            boolean consentGranted = checkKakaoConsent(target.userId());
            if (!consentGranted) {
                log.warn("카카오 동의 없음. userId={}", target.userId());
                history.markAsFailed(FailureReason.consentNotGranted("카카오 알림톡 수신 동의가 없습니다."));
                historyPort.save(history);
                return historyId;
            }
        }

        try {
            kakaoPort.send(target.phoneNumber(), templateCode, variables);
            history.markAsSuccess();
            log.debug("알림톡 발송 성공. historyId={}, userId={}", historyId, target.userId());
        } catch (Exception e) {
            log.error("알림톡 발송 실패. historyId={}, userId={}", historyId, target.userId(), e);
            history.markAsFailed(FailureReason.providerError(e.getMessage()));
        }

        historyPort.save(history);
        return historyId;
    }

    private boolean checkKakaoConsent(String userId) {
        return consentCachePort.findByUserId(userId)
                .map(UserConsent::getConsentSettings)
                .map(settings -> settings.isMarketingConsent() && settings.isKakaoConsent())
                .orElse(false);
    }
}
