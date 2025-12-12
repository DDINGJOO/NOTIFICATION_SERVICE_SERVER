package com.teambind.springproject.domain.model.notification;

import lombok.Getter;

/**
 * 발송 실패 사유 Value Object
 */
@Getter
public class FailureReason {

    private final FailureType type;
    private final String message;

    private FailureReason(FailureType type, String message) {
        this.type = type;
        this.message = message;
    }

    public static FailureReason of(FailureType type, String message) {
        return new FailureReason(type, message);
    }

    public static FailureReason networkError(String message) {
        return new FailureReason(FailureType.NETWORK_ERROR, message);
    }

    public static FailureReason invalidRecipient(String message) {
        return new FailureReason(FailureType.INVALID_RECIPIENT, message);
    }

    public static FailureReason providerError(String message) {
        return new FailureReason(FailureType.PROVIDER_ERROR, message);
    }

    public static FailureReason rateLimitExceeded(String message) {
        return new FailureReason(FailureType.RATE_LIMIT_EXCEEDED, message);
    }

    public static FailureReason consentNotGranted(String message) {
        return new FailureReason(FailureType.CONSENT_NOT_GRANTED, message);
    }

    public static FailureReason unknown(String message) {
        return new FailureReason(FailureType.UNKNOWN, message);
    }

    /**
     * 재시도 가능 여부 판단
     */
    public boolean isRetryable() {
        return type.isRetryable();
    }

    public enum FailureType {
        /**
         * 네트워크 오류 - 재시도 가능
         */
        NETWORK_ERROR(true),

        /**
         * 잘못된 수신자 - 재시도 불가
         */
        INVALID_RECIPIENT(false),

        /**
         * 외부 서비스 오류 - 재시도 가능
         */
        PROVIDER_ERROR(true),

        /**
         * 발송 한도 초과 - 재시도 가능 (시간 후)
         */
        RATE_LIMIT_EXCEEDED(true),

        /**
         * 동의 없음 - 재시도 불가
         */
        CONSENT_NOT_GRANTED(false),

        /**
         * 알 수 없는 오류 - 재시도 불가
         */
        UNKNOWN(false);

        private final boolean retryable;

        FailureType(boolean retryable) {
            this.retryable = retryable;
        }

        public boolean isRetryable() {
            return retryable;
        }
    }
}
