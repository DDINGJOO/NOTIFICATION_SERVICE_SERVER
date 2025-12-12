package com.teambind.springproject.domain.model.notification;

/**
 * 알림 유형
 */
public enum NotificationType {
    /**
     * 트랜잭션 알림 (인증, 주문 확인 등) - 동의 불필요
     */
    TRANSACTIONAL,

    /**
     * 마케팅 알림 (프로모션, 이벤트 등) - 동의 필요
     */
    MARKETING,

    /**
     * 서비스 알림 (공지, 시스템 안내 등)
     */
    SERVICE
}
