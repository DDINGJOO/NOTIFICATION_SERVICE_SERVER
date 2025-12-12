package com.teambind.springproject.adapter.in.scheduler;

import com.teambind.springproject.application.port.in.NotificationRetryUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 알림 재시도 스케줄러
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationRetryScheduler {

    private final NotificationRetryUseCase retryUseCase;

    /**
     * 1분마다 재시도 대상 알림 처리
     */
    @Scheduled(fixedDelay = 60000)
    public void processRetryableNotifications() {
        log.debug("알림 재시도 스케줄러 실행");
        retryUseCase.processRetryableNotifications();
    }
}
