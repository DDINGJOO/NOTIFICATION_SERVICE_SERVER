package com.teambind.springproject.domain.model.notification;

/**
 * 알림 발송 상태
 */
public enum NotificationStatus {
    /**
     * 발송 대기
     */
    PENDING,

    /**
     * 발송 성공
     */
    SUCCESS,

    /**
     * 발송 실패
     */
    FAILED,

    /**
     * 재시도 중
     */
    RETRYING,

    /**
     * 최종 실패 (재시도 횟수 초과)
     */
    DEAD_LETTER
}
