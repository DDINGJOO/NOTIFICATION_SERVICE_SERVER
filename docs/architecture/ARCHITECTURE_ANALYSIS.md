# Notification Service - 아키텍처 분석

---

## 개요

이 문서는 Notification Service의 아키텍처 설계 결정 과정과 근거를 기록합니다.

---

## 아키텍처 패턴 선정

### 선정: Hexagonal Architecture + DDD + Event-Driven

#### 선정 이유

| 관점 | Hexagonal Architecture 적합성 |
|------|------------------------------|
| **다채널 지원** | 각 채널(Email, SMS, Push, InApp)을 독립된 Adapter로 분리 가능 |
| **외부 연동 변경** | 외부 서비스(Solapi, FCM) 변경 시 Adapter만 교체 |
| **테스트 용이성** | Port 기반으로 Mock 주입 용이 |
| **확장성** | 새로운 채널 추가 시 Adapter만 추가 |

#### 대안 비교

| 패턴 | 장점 | 단점 | 적합성 |
|------|------|------|--------|
| **Layered Architecture** | 단순함, 익숙함 | 외부 연동 변경 시 변경 범위 큼 | 보통 |
| **Hexagonal Architecture** | 외부 의존성 분리, 확장성 | 초기 복잡도 | **높음** |
| **Clean Architecture** | 의존성 규칙 명확 | 레이어 많음 | 높음 |

---

## 전체 아키텍처 구조

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           Primary Adapters (Inbound)                     │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────────────┐   │
│  │  REST API    │  │ Kafka        │  │ Kafka Consumer               │   │
│  │  Controller  │  │ Consumer     │  │ (User Consent Event)         │   │
│  │              │  │ (Notification│  │                              │   │
│  │              │  │  Request)    │  │                              │   │
│  └──────┬───────┘  └──────┬───────┘  └──────────────┬───────────────┘   │
└─────────┼─────────────────┼─────────────────────────┼───────────────────┘
          │                 │                         │
          ▼                 ▼                         ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                              Inbound Ports                               │
│  ┌──────────────────────┐  ┌──────────────────────────────────────┐     │
│  │ NotificationUseCase  │  │ UserConsentUseCase                   │     │
│  │ - sendNotification() │  │ - updateConsent()                    │     │
│  │ - sendBulk()         │  │ - getConsent()                       │     │
│  └──────────┬───────────┘  └──────────────────┬───────────────────┘     │
└─────────────┼─────────────────────────────────┼─────────────────────────┘
              │                                 │
              ▼                                 ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                           Domain Layer (Core)                            │
│  ┌────────────────────────────────────────────────────────────────────┐ │
│  │                     Application Services                           │ │
│  │  ┌─────────────────────┐  ┌─────────────────────────────────────┐  │ │
│  │  │NotificationService  │  │ UserConsentService                  │  │ │
│  │  │                     │  │                                     │  │ │
│  │  └─────────────────────┘  └─────────────────────────────────────┘  │ │
│  └────────────────────────────────────────────────────────────────────┘ │
│  ┌────────────────────────────────────────────────────────────────────┐ │
│  │                         Domain Model                               │ │
│  │  ┌───────────────┐ ┌───────────────┐ ┌───────────────────────────┐ │ │
│  │  │ Notification  │ │ UserConsent   │ │ NotificationHistory       │ │ │
│  │  │ (Aggregate)   │ │ (Aggregate)   │ │ (Aggregate)               │ │ │
│  │  └───────────────┘ └───────────────┘ └───────────────────────────┘ │ │
│  └────────────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────────┘
              │                                 │
              ▼                                 ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                             Outbound Ports                               │
│  ┌─────────────────┐ ┌─────────────────┐ ┌────────────────────────────┐ │
│  │ NotificationPort│ │ ConsentPort     │ │ HistoryPort                │ │
│  │ - send()        │ │ - save()        │ │ - save()                   │ │
│  │                 │ │ - findByUserId()│ │ - findByFilter()           │ │
│  └────────┬────────┘ └────────┬────────┘ └─────────────┬──────────────┘ │
└───────────┼───────────────────┼────────────────────────┼────────────────┘
            │                   │                        │
            ▼                   ▼                        ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                        Secondary Adapters (Outbound)                     │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌───────────────┐  │
│  │  Email   │ │   SMS    │ │   Push   │ │  InApp   │ │  PostgreSQL   │  │
│  │ Adapter  │ │ Adapter  │ │ Adapter  │ │ Adapter  │ │  Repository   │  │
│  │ (SMTP)   │ │ (Solapi) │ │ (FCM)    │ │          │ │               │  │
│  └────┬─────┘ └────┬─────┘ └────┬─────┘ └────┬─────┘ └───────┬───────┘  │
└───────┼────────────┼────────────┼────────────┼───────────────┼──────────┘
        │            │            │            │               │
        ▼            ▼            ▼            ▼               ▼
   Gmail SMTP    Solapi API     FCM API    WebSocket/      PostgreSQL
                                           SSE/Polling
```

---

## 레이어별 책임

### Primary Adapters (Inbound)

| Adapter | 책임 | 구현 |
|---------|------|------|
| **REST Controller** | 동기 발송 요청 수신 | Spring MVC |
| **Kafka Consumer (Notification)** | 비동기 발송 요청 수신 | Spring Kafka |
| **Kafka Consumer (Consent)** | 사용자 동의 정보 수신 | Spring Kafka |

### Domain Layer

| 구성요소 | 책임 |
|----------|------|
| **Application Service** | 유스케이스 조율, 트랜잭션 관리 |
| **Domain Model** | 비즈니스 로직, 불변식 유지 |
| **Domain Service** | 여러 Aggregate 간 로직 |

### Secondary Adapters (Outbound)

| Adapter | 책임 | 외부 시스템 |
|---------|------|------------|
| **EmailAdapter** | 이메일 발송 | Gmail SMTP |
| **SmsAdapter** | SMS 발송 | Solapi API |
| **PushAdapter** | 앱 푸시 발송 | FCM |
| **InAppAdapter** | 인앱 알림 저장/전달 | 자체 구현 |
| **PostgresRepository** | 데이터 영속화 | PostgreSQL |
| **RedisRepository** | 캐싱, 중복 방지 | Redis |

---

## 알림 발송 흐름

### 동기 발송 (REST API)

```
Client → REST Controller → NotificationUseCase → NotificationService
                                                        │
                                    ┌───────────────────┴───────────────────┐
                                    ▼                                       ▼
                           ConsentRepository                        ChannelAdapter
                           (동의 확인)                               (채널별 발송)
                                    │                                       │
                                    └───────────────────┬───────────────────┘
                                                        ▼
                                                HistoryRepository
                                                (이력 저장)
                                                        │
                                                        ▼
                                                   Response
```

### 비동기 발송 (Kafka)

```
Producer → Kafka Topic → Kafka Consumer → NotificationUseCase → NotificationService
                                                                        │
                                                    ┌───────────────────┴───────────────────┐
                                                    ▼                                       ▼
                                           ConsentRepository                        ChannelAdapter
                                           (동의 확인)                               (채널별 발송)
                                                    │                                       │
                                                    └───────────────────┬───────────────────┘
                                                                        ▼
                                                                HistoryRepository
                                                                (이력 저장)
```

---

## 채널별 Adapter 설계

### Strategy Pattern 적용

```java
public interface NotificationPort {
    NotificationResult send(Notification notification);
    boolean supports(ChannelType channel);
}

// 구현체
public class EmailAdapter implements NotificationPort { ... }
public class SmsAdapter implements NotificationPort { ... }
public class PushAdapter implements NotificationPort { ... }
public class InAppAdapter implements NotificationPort { ... }
```

### 채널 라우팅

```java
public class NotificationDispatcher {
    private final List<NotificationPort> adapters;

    public NotificationResult dispatch(Notification notification) {
        return adapters.stream()
            .filter(adapter -> adapter.supports(notification.getChannel()))
            .findFirst()
            .orElseThrow(() -> new UnsupportedChannelException(...))
            .send(notification);
    }
}
```

---

## 인앱 알림 전달 방식 분석

### 옵션 비교

| 방식 | 장점 | 단점 | 복잡도 |
|------|------|------|--------|
| **WebSocket** | 실시간 양방향 통신, 연결 상태 확인 가능 | 연결 관리 복잡, 스케일 아웃 시 세션 공유 필요 | 높음 |
| **SSE (Server-Sent Events)** | 단방향으로 충분, HTTP 기반, 구현 단순 | 양방향 불가, 브라우저 연결 수 제한 | 중간 |
| **Polling** | 가장 단순, 인프라 부담 적음 | 실시간성 낮음, 불필요한 요청 발생 | 낮음 |

### 권장: SSE + Polling Fallback

**이유:**
1. 인앱 알림은 서버→클라이언트 단방향으로 충분
2. HTTP 기반으로 기존 인프라 활용 가능
3. 폴링을 fallback으로 두어 안정성 확보

**구현 방향:**
- 기본: SSE로 실시간 푸시
- Fallback: 클라이언트가 주기적으로 알림 목록 조회
- 읽음 처리: REST API로 별도 처리

---

## 재시도 전략

### 방식: Exponential Backoff with Jitter

```
재시도 1회: 1분 + random(0~30초)
재시도 2회: 5분 + random(0~30초)
재시도 3회: 15분 + random(0~30초)
```

### 구현 방안

| 방안 | 장점 | 단점 |
|------|------|------|
| **Spring Retry** | 간단한 설정, 어노테이션 기반 | 동기 처리에 적합 |
| **Kafka Retry Topic** | 비동기 재시도, 처리량 분리 | Kafka 토픽 관리 필요 |
| **Scheduled Job** | DB 기반 상태 관리 | 별도 스케줄러 필요 |

### 권장: Kafka Retry Topic + Spring Retry 병행

- **동기 발송**: Spring Retry (즉시 재시도)
- **비동기 발송**: Kafka Retry Topic (지연 재시도)

---

## Dead Letter Queue (DLQ)

### 용도
- 재시도 횟수 초과 메시지 저장
- 영구 실패 메시지 분석
- 수동 재처리 대상 관리

### 구조
```
notification-request (메인 토픽)
    ↓ 실패
notification-request-retry-1 (1차 재시도)
    ↓ 실패
notification-request-retry-2 (2차 재시도)
    ↓ 실패
notification-request-dlq (Dead Letter Queue)
```

---

## 데이터 저장소 설계

### PostgreSQL 사용 영역

| 테이블 | 용도 |
|--------|------|
| `user_consent` | 사용자 수신 동의 정보 |
| `notification_history` | 발송 이력 |
| `notification_template` | 알림 템플릿 (추후) |
| `in_app_notification` | 인앱 알림 목록 |

### Redis 사용 영역

| Key Pattern | 용도 | TTL |
|-------------|------|-----|
| `consent:{userId}` | 동의 정보 캐싱 | 1시간 |
| `dedup:{requestId}` | 중복 발송 방지 | 24시간 |
| `fcm_token:{userId}` | FCM 토큰 캐싱 | 7일 |

---

## 패키지 구조 (권장)

```
com.bander.notification
├── adapter
│   ├── in
│   │   ├── rest          # REST Controller
│   │   └── kafka         # Kafka Consumer
│   └── out
│       ├── email         # Email Adapter (SMTP)
│       ├── sms           # SMS Adapter (Solapi)
│       ├── push          # Push Adapter (FCM)
│       ├── inapp         # InApp Adapter
│       └── persistence   # Repository 구현체
│
├── application
│   ├── port
│   │   ├── in            # Inbound Port (UseCase)
│   │   └── out           # Outbound Port
│   └── service           # Application Service
│
├── domain
│   ├── model             # Entity, Aggregate, Value Object
│   ├── service           # Domain Service
│   └── event             # Domain Event
│
└── infrastructure
    ├── config            # Spring Configuration
    └── common            # 공통 유틸리티
```

---

## 확장 고려사항

### 새로운 채널 추가 시
1. `NotificationPort` 구현체 추가
2. `ChannelType` enum에 채널 추가
3. 설정 파일에 채널별 설정 추가

### 외부 서비스 변경 시
1. 해당 Adapter만 교체
2. Port 인터페이스는 유지
3. 비즈니스 로직 변경 없음

---

## 관련 문서

- [NOTIFICATION_REQUIREMENTS.md](../requirements/NOTIFICATION_REQUIREMENTS.md) - 비즈니스 요구사항
- [DOMAIN_MODEL_DESIGN.md](DOMAIN_MODEL_DESIGN.md) - 도메인 모델 설계

---

**Last Updated**: 2025-12-11
