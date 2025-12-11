# Notification Service - 도메인 모델 설계

---

## 개요

이 문서는 Notification Service의 도메인 모델 구조를 정의합니다.
DDD(Domain-Driven Design) 원칙에 따라 Aggregate, Entity, Value Object를 설계합니다.

---

## Aggregate 개요

```
┌─────────────────────────────────────────────────────────────────────────┐
│                            Domain Model                                  │
│                                                                         │
│  ┌───────────────────┐  ┌───────────────────┐  ┌─────────────────────┐  │
│  │   Notification    │  │   UserConsent     │  │ NotificationHistory │  │
│  │   (Aggregate)     │  │   (Aggregate)     │  │   (Aggregate)       │  │
│  │                   │  │                   │  │                     │  │
│  │ - 발송 요청 처리  │  │ - 수신 동의 관리  │  │ - 발송 이력 저장    │  │
│  │ - 채널 라우팅     │  │ - 동의 여부 확인  │  │ - 재시도 상태 관리  │  │
│  │ - 유형별 규칙     │  │ - 야간 수신 확인  │  │ - 통계 집계         │  │
│  └───────────────────┘  └───────────────────┘  └─────────────────────┘  │
│                                                                         │
│  ┌───────────────────┐  ┌───────────────────┐                           │
│  │  InAppNotification│  │ NotificationTemplate│  (추후 확장)            │
│  │   (Aggregate)     │  │   (Aggregate)       │                         │
│  │                   │  │                     │                         │
│  │ - 인앱 알림 관리  │  │ - 템플릿 관리       │                         │
│  │ - 읽음 상태 관리  │  │ - 변수 치환         │                         │
│  └───────────────────┘  └───────────────────┘                           │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## Aggregate 1: Notification

### 구조

```
Notification (Aggregate Root)
├── NotificationId (Value Object)
├── Recipient (Value Object)
│   ├── userId: String
│   ├── email: String (nullable)
│   ├── phoneNumber: String (nullable)
│   └── fcmToken: String (nullable)
├── NotificationChannel (Enum)
├── NotificationType (Enum)
├── NotificationContent (Value Object)
│   ├── title: String
│   ├── body: String
│   └── metadata: Map<String, Object>
├── RequestedAt (Value Object)
└── RequestSource (Value Object)
    ├── sourceService: String
    └── correlationId: String
```

### 주요 책임

1. **발송 가능 여부 판단**
   - 수신자 정보 유효성 검증
   - 채널별 필수 정보 확인

2. **알림 유형별 규칙 적용**
   - 트랜잭션: 즉시 발송
   - 서비스: 동의 확인 필요
   - 광고: 동의 + 야간 규칙 적용

3. **채널 라우팅**
   - 적합한 채널 Adapter 선택

### Value Objects

#### NotificationId
```java
public record NotificationId(String value) {
    public NotificationId {
        Objects.requireNonNull(value, "NotificationId cannot be null");
    }

    public static NotificationId generate() {
        return new NotificationId(UUID.randomUUID().toString());
    }
}
```

#### Recipient
```java
public record Recipient(
    String userId,
    String email,
    String phoneNumber,
    String fcmToken
) {
    public boolean hasEmail() {
        return email != null && !email.isBlank();
    }

    public boolean hasPhoneNumber() {
        return phoneNumber != null && !phoneNumber.isBlank();
    }

    public boolean hasFcmToken() {
        return fcmToken != null && !fcmToken.isBlank();
    }

    public boolean canReceive(NotificationChannel channel) {
        return switch (channel) {
            case EMAIL -> hasEmail();
            case SMS -> hasPhoneNumber();
            case PUSH -> hasFcmToken();
            case IN_APP -> userId != null;
        };
    }
}
```

#### NotificationContent
```java
public record NotificationContent(
    String title,
    String body,
    Map<String, Object> metadata
) {
    public NotificationContent {
        Objects.requireNonNull(title, "Title cannot be null");
        Objects.requireNonNull(body, "Body cannot be null");
        metadata = metadata != null ? Map.copyOf(metadata) : Map.of();
    }
}
```

### Enums

#### NotificationChannel
```java
public enum NotificationChannel {
    EMAIL,
    SMS,
    PUSH,
    IN_APP
}
```

#### NotificationType
```java
public enum NotificationType {
    TRANSACTION,  // 트랜잭션 알림 (동의 불필요)
    SERVICE,      // 서비스 알림 (서비스 동의 필요)
    MARKETING     // 광고/마케팅 알림 (광고 동의 필요)
}
```

---

## Aggregate 2: UserConsent

### 구조

```
UserConsent (Aggregate Root)
├── UserConsentId (Value Object)
├── UserId (Value Object)
├── ConsentSettings (Value Object)
│   ├── serviceNotification: boolean
│   ├── marketingNotification: boolean
│   └── nightTimeMarketing: boolean
├── ConsentHistory (List<ConsentChange>)
│   ├── changedAt: Instant
│   ├── previousValue: ConsentSettings
│   └── newValue: ConsentSettings
├── CreatedAt: Instant
└── UpdatedAt: Instant
```

### 주요 책임

1. **동의 상태 관리**
   - 서비스 알림 수신 동의
   - 광고성 정보 수신 동의
   - 야간 광고 수신 동의

2. **동의 여부 확인**
   - 알림 유형별 발송 가능 여부 판단
   - 야간 시간대 발송 가능 여부 판단

3. **동의 변경 이력 관리**
   - 변경 시점 기록
   - 이전/이후 값 추적

### Value Objects

#### ConsentSettings
```java
public record ConsentSettings(
    boolean serviceNotification,
    boolean marketingNotification,
    boolean nightTimeMarketing
) {
    public static ConsentSettings defaultSettings() {
        return new ConsentSettings(true, false, false);
    }

    public boolean canReceive(NotificationType type, boolean isNightTime) {
        return switch (type) {
            case TRANSACTION -> true;
            case SERVICE -> serviceNotification;
            case MARKETING -> {
                if (!marketingNotification) yield false;
                if (isNightTime && !nightTimeMarketing) yield false;
                yield true;
            }
        };
    }
}
```

### 비즈니스 규칙

#### 야간 시간대 판단
```java
public class NightTimeChecker {
    private static final LocalTime NIGHT_START = LocalTime.of(21, 0);
    private static final LocalTime NIGHT_END = LocalTime.of(8, 0);
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    public static boolean isNightTime() {
        LocalTime now = LocalTime.now(KST);
        return now.isAfter(NIGHT_START) || now.isBefore(NIGHT_END);
    }
}
```

---

## Aggregate 3: NotificationHistory

### 구조

```
NotificationHistory (Aggregate Root)
├── HistoryId (Value Object)
├── NotificationId (Value Object)
├── Recipient (Value Object)
├── NotificationChannel (Enum)
├── NotificationType (Enum)
├── NotificationContent (Value Object)
├── SendStatus (Enum)
├── FailureReason (Value Object, nullable)
├── RetryCount: int
├── SentAt: Instant (nullable)
├── CreatedAt: Instant
└── ChannelMetadata (Value Object)
    └── Map<String, Object>  # 채널별 응답 정보
```

### 주요 책임

1. **발송 결과 기록**
   - 성공/실패 상태 저장
   - 실패 사유 기록
   - 채널별 메타데이터 저장

2. **재시도 상태 관리**
   - 재시도 횟수 추적
   - 재시도 대상 여부 판단

3. **통계 집계 지원**
   - 기간별 발송량 조회
   - 채널별/유형별 성공률 계산

### Enums

#### SendStatus
```java
public enum SendStatus {
    PENDING,    // 발송 대기
    SENT,       // 발송 완료
    FAILED,     // 발송 실패
    RETRYING,   // 재시도 중
    DELIVERED   // 전달 확인 (지원하는 채널만)
}
```

### Value Objects

#### FailureReason
```java
public record FailureReason(
    String code,
    String message,
    boolean retryable
) {
    public static FailureReason network(String message) {
        return new FailureReason("NETWORK_ERROR", message, true);
    }

    public static FailureReason invalidRecipient(String message) {
        return new FailureReason("INVALID_RECIPIENT", message, false);
    }

    public static FailureReason externalService(String message) {
        return new FailureReason("EXTERNAL_SERVICE_ERROR", message, true);
    }

    public static FailureReason timeout(String message) {
        return new FailureReason("TIMEOUT", message, true);
    }
}
```

---

## Aggregate 4: InAppNotification

### 구조

```
InAppNotification (Aggregate Root)
├── InAppNotificationId (Value Object)
├── UserId (Value Object)
├── NotificationContent (Value Object)
│   ├── title: String
│   ├── body: String
│   └── actionUrl: String (nullable)
├── ReadStatus (Enum)
├── CreatedAt: Instant
└── ReadAt: Instant (nullable)
```

### 주요 책임

1. **인앱 알림 저장**
   - 사용자별 알림 목록 관리
   - 알림 내용 및 액션 URL 저장

2. **읽음 상태 관리**
   - 미읽음/읽음 상태 전환
   - 읽음 시각 기록

3. **알림 목록 조회**
   - 사용자별 알림 목록
   - 미읽음 알림 개수

### Enums

#### ReadStatus
```java
public enum ReadStatus {
    UNREAD,
    READ
}
```

---

## Domain Service

### NotificationDomainService

여러 Aggregate 간 조율이 필요한 로직을 처리합니다.

```java
public class NotificationDomainService {

    /**
     * 알림 발송 가능 여부 확인
     * - 수신자 정보 유효성
     * - 수신 동의 여부
     * - 야간 시간대 규칙
     */
    public SendabilityResult checkSendability(
        Notification notification,
        UserConsent consent
    ) {
        // 1. 수신자가 해당 채널로 받을 수 있는지
        if (!notification.getRecipient().canReceive(notification.getChannel())) {
            return SendabilityResult.fail("수신자 정보 누락");
        }

        // 2. 동의 여부 확인
        boolean isNightTime = NightTimeChecker.isNightTime();
        if (!consent.getSettings().canReceive(notification.getType(), isNightTime)) {
            return SendabilityResult.fail("수신 동의 없음");
        }

        return SendabilityResult.success();
    }
}
```

---

## Entity 관계도

```
┌─────────────────┐         ┌─────────────────┐
│  Notification   │         │   UserConsent   │
│                 │         │                 │
│  - id           │◄───────►│  - id           │
│  - recipient    │  참조    │  - userId       │
│  - channel      │         │  - settings     │
│  - type         │         │                 │
│  - content      │         └─────────────────┘
└────────┬────────┘
         │
         │ 발송 후 생성
         ▼
┌─────────────────┐
│Notification     │
│History          │
│                 │
│  - id           │
│  - notification │
│  - status       │
│  - failureReason│
│  - retryCount   │
└─────────────────┘
         │
         │ IN_APP 채널인 경우
         ▼
┌─────────────────┐
│InAppNotification│
│                 │
│  - id           │
│  - userId       │
│  - content      │
│  - readStatus   │
└─────────────────┘
```

---

## Repository Interface (Outbound Port)

### NotificationHistoryRepository
```java
public interface NotificationHistoryRepository {
    void save(NotificationHistory history);
    Optional<NotificationHistory> findById(HistoryId id);
    List<NotificationHistory> findByRecipientUserId(String userId, Pageable pageable);
    List<NotificationHistory> findByStatusAndRetryable(SendStatus status, int maxRetryCount);
    NotificationStatistics getStatistics(LocalDate from, LocalDate to);
}
```

### UserConsentRepository
```java
public interface UserConsentRepository {
    void save(UserConsent consent);
    Optional<UserConsent> findByUserId(String userId);
    void deleteByUserId(String userId);
}
```

### InAppNotificationRepository
```java
public interface InAppNotificationRepository {
    void save(InAppNotification notification);
    List<InAppNotification> findByUserId(String userId, Pageable pageable);
    List<InAppNotification> findUnreadByUserId(String userId);
    long countUnreadByUserId(String userId);
    void markAsRead(InAppNotificationId id);
    void markAllAsRead(String userId);
}
```

---

## 팩토리 패턴 적용

### NotificationFactory
```java
public class NotificationFactory {

    private NotificationFactory() {}

    public static Notification createTransaction(
        Recipient recipient,
        NotificationChannel channel,
        NotificationContent content,
        RequestSource source
    ) {
        return Notification.builder()
            .id(NotificationId.generate())
            .recipient(recipient)
            .channel(channel)
            .type(NotificationType.TRANSACTION)
            .content(content)
            .requestedAt(Instant.now())
            .requestSource(source)
            .build();
    }

    public static Notification createService(
        Recipient recipient,
        NotificationChannel channel,
        NotificationContent content,
        RequestSource source
    ) {
        return Notification.builder()
            .id(NotificationId.generate())
            .recipient(recipient)
            .channel(channel)
            .type(NotificationType.SERVICE)
            .content(content)
            .requestedAt(Instant.now())
            .requestSource(source)
            .build();
    }

    public static Notification createMarketing(
        Recipient recipient,
        NotificationChannel channel,
        NotificationContent content,
        RequestSource source
    ) {
        // 광고성 알림은 제목에 (광고) 표기 강제
        NotificationContent marketingContent = new NotificationContent(
            "(광고) " + content.title(),
            content.body(),
            content.metadata()
        );

        return Notification.builder()
            .id(NotificationId.generate())
            .recipient(recipient)
            .channel(channel)
            .type(NotificationType.MARKETING)
            .content(marketingContent)
            .requestedAt(Instant.now())
            .requestSource(source)
            .build();
    }
}
```

---

## 관련 문서

- [NOTIFICATION_REQUIREMENTS.md](../requirements/NOTIFICATION_REQUIREMENTS.md) - 비즈니스 요구사항
- [ARCHITECTURE_ANALYSIS.md](ARCHITECTURE_ANALYSIS.md) - 아키텍처 분석

---

**Last Updated**: 2025-12-11
