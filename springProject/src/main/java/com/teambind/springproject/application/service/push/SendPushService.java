package com.teambind.springproject.application.service.push;

import com.teambind.springproject.application.port.in.SendPushUseCase;
import com.teambind.springproject.application.port.out.NotificationHistoryPort;
import com.teambind.springproject.application.port.out.PushPort;
import com.teambind.springproject.common.util.generator.PrimaryKeyGenerator;
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
 * 푸시 알림 발송 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SendPushService implements SendPushUseCase {

    private final PushPort pushPort;
    private final NotificationHistoryPort historyPort;
    private final PrimaryKeyGenerator keyGenerator;

    @Override
    @Transactional
    public List<Long> sendPush(NotificationType type, List<PushTarget> targets, String title, String body, Map<String, String> data) {
        log.info("푸시 알림 일괄 발송 요청. type={}, targetCount={}", type, targets.size());

        List<Long> historyIds = new ArrayList<>();

        for (PushTarget target : targets) {
            Long historyId = sendSinglePush(type, target, title, body, data);
            historyIds.add(historyId);
        }

        log.info("푸시 알림 일괄 발송 완료. totalCount={}", historyIds.size());
        return historyIds;
    }

    private Long sendSinglePush(NotificationType type, PushTarget target, String title, String body, Map<String, String> data) {
        Long historyId = keyGenerator.generateLongKey();
        String content = formatContent(title, body);

        NotificationHistory history = NotificationHistory.create(
                historyId,
                target.userId(),
                target.deviceToken(),
                NotificationChannel.PUSH,
                type,
                content
        );

        try {
            pushPort.send(target.deviceToken(), title, body, data);
            history.markAsSuccess();
            log.debug("푸시 알림 발송 성공. historyId={}, userId={}", historyId, target.userId());
        } catch (Exception e) {
            log.error("푸시 알림 발송 실패. historyId={}, userId={}", historyId, target.userId(), e);
            history.markAsFailed(FailureReason.providerError(e.getMessage()));
        }

        historyPort.save(history);
        return historyId;
    }

    private String formatContent(String title, String body) {
        return String.format("[%s] %s", title, body);
    }
}
