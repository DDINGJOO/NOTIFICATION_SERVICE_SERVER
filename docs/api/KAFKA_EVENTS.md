# Notification Service - Kafka Event 명세

> **Version**: 1.0.0
> **Consumer Group**: `notification-consumer-group`

---

## 목차

- [개요](#개요)
- [일괄 발송 이벤트](#일괄-발송-이벤트)
  - [SMS 발송 이벤트](#sms-발송-이벤트)
  - [Email 발송 이벤트](#email-발송-이벤트)
  - [Push 발송 이벤트](#push-발송-이벤트)
  - [Kakao 발송 이벤트](#kakao-발송-이벤트)
- [인증 이벤트 (기존 호환)](#인증-이벤트-기존-호환)
  - [이메일 인증 이벤트](#이메일-인증-이벤트)
  - [SMS 인증 이벤트](#sms-인증-이벤트)
- [동의 관련 이벤트](#동의-관련-이벤트)

---

## 개요

### 토픽 목록

| 토픽 | 용도 | Producer | Consumer |
|------|------|----------|----------|
| `sms-send-request` | SMS 일괄 발송 | 타 서비스 | Notification |
| `email-send-request` | Email 일괄 발송 | 타 서비스 | Notification |
| `push-send-request` | Push 일괄 발송 | 타 서비스 | Notification |
| `kakao-send-request` | 알림톡 일괄 발송 | 타 서비스 | Notification |
| `email-confirm-request` | 이메일 인증번호 | Auth | Notification |
| `sms-confirm-request` | SMS 인증번호 | Auth | Notification |
| `phone-number-verified` | 전화번호 인증 완료 | Auth | Notification |

### 공통 구조

모든 일괄 발송 이벤트는 동일한 기본 구조를 따릅니다:

```json
{
  "type": "TRANSACTIONAL | MARKETING | SERVICE",
  "targets": [
    { "userId": "...", "recipient": "..." }
  ],
  ...channel-specific-fields
}
```

---

## 일괄 발송 이벤트

### SMS 발송 이벤트

**Topic**: `sms-send-request`

**Schema**:

```json
{
  "type": "MARKETING",
  "targets": [
    {
      "userId": "user-001",
      "phoneNumber": "01012345678"
    },
    {
      "userId": "user-002",
      "phoneNumber": "01087654321"
    }
  ],
  "content": "[BANDER] 이번 주 특가! 50% 할인 이벤트 진행 중"
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `type` | `String` | O | `TRANSACTIONAL`, `MARKETING`, `SERVICE` |
| `targets` | `Array` | O | 발송 대상 목록 |
| `targets[].userId` | `String` | O | 사용자 ID |
| `targets[].phoneNumber` | `String` | O | 전화번호 |
| `content` | `String` | O | 메시지 내용 |

**처리 로직**:
1. `targets` 루프
2. `MARKETING` 타입인 경우 동의 확인 (`marketingConsent && smsConsent`)
3. Solapi API로 SMS 발송
4. 이력 저장

---

### Email 발송 이벤트

**Topic**: `email-send-request`

**Schema**:

```json
{
  "type": "MARKETING",
  "targets": [
    {
      "userId": "user-001",
      "email": "user1@example.com"
    },
    {
      "userId": "user-002",
      "email": "user2@example.com"
    }
  ],
  "title": "이번 주 특가 프로모션",
  "content": "<html><body><h1>50% 할인 이벤트</h1><p>지금 바로 확인하세요!</p></body></html>"
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `type` | `String` | O | 알림 타입 |
| `targets` | `Array` | O | 발송 대상 목록 |
| `targets[].userId` | `String` | O | 사용자 ID |
| `targets[].email` | `String` | O | 이메일 주소 |
| `title` | `String` | O | 이메일 제목 |
| `content` | `String` | O | 이메일 내용 (HTML) |

**처리 로직**:
1. `targets` 루프
2. `MARKETING` 타입인 경우 동의 확인 (`marketingConsent && emailConsent`)
3. SMTP로 이메일 발송
4. 이력 저장

---

### Push 발송 이벤트

**Topic**: `push-send-request`

**Schema**:

```json
{
  "type": "TRANSACTIONAL",
  "targets": [
    {
      "userId": "user-001",
      "deviceToken": "fcm_token_xxxxxxx"
    }
  ],
  "title": "예약 확인",
  "body": "내일 14:00 예약이 확정되었습니다.",
  "data": {
    "reservationId": "12345",
    "action": "VIEW_RESERVATION"
  }
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `type` | `String` | O | 알림 타입 |
| `targets` | `Array` | O | 발송 대상 목록 |
| `targets[].userId` | `String` | O | 사용자 ID |
| `targets[].deviceToken` | `String` | O | FCM 디바이스 토큰 |
| `title` | `String` | O | 푸시 제목 |
| `body` | `String` | O | 푸시 본문 |
| `data` | `Object` | X | 추가 데이터 |

**처리 로직**:
1. `targets` 루프
2. FCM API로 푸시 발송
3. 이력 저장

---

### Kakao 발송 이벤트

**Topic**: `kakao-send-request`

**Schema**:

```json
{
  "type": "TRANSACTIONAL",
  "targets": [
    {
      "userId": "user-001",
      "phoneNumber": "01012345678"
    }
  ],
  "templateCode": "RESERVATION_CONFIRM_001",
  "variables": {
    "customerName": "홍길동",
    "reservationDate": "2025-12-15",
    "reservationTime": "14:00",
    "storeName": "강남점"
  }
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `type` | `String` | O | 알림 타입 |
| `targets` | `Array` | O | 발송 대상 목록 |
| `targets[].userId` | `String` | O | 사용자 ID |
| `targets[].phoneNumber` | `String` | O | 전화번호 |
| `templateCode` | `String` | O | 알림톡 템플릿 코드 |
| `variables` | `Object` | X | 템플릿 변수 |

**처리 로직**:
1. `targets` 루프
2. `MARKETING` 타입인 경우 동의 확인 (`marketingConsent && kakaoConsent`)
3. Solapi API로 알림톡 발송
4. 이력 저장

---

## 인증 이벤트 (기존 호환)

Auth 서버와의 호환을 위해 유지되는 인증번호 발송 이벤트입니다.

### 이메일 인증 이벤트

**Topic**: `email-confirm-request`

**Schema**:

```json
{
  "email": "user@example.com",
  "code": "123456"
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `email` | `String` | O | 이메일 주소 |
| `code` | `String` | O | 인증 코드 (6자리) |

**처리**: 이메일 인증번호 발송 (동의 확인 없음)

---

### SMS 인증 이벤트

**Topic**: `sms-confirm-request`

**Schema**:

```json
{
  "phoneNumber": "01012345678",
  "code": "123456"
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `phoneNumber` | `String` | O | 전화번호 |
| `code` | `String` | O | 인증 코드 (6자리) |

**처리**: SMS 인증번호 발송 (동의 확인 없음)

---

## 동의 관련 이벤트

### 전화번호 인증 완료 이벤트

**Topic**: `phone-number-verified`

**Schema**:

```json
{
  "userId": "user-123",
  "phoneNumber": "01012345678"
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `userId` | `String` | O | 사용자 ID |
| `phoneNumber` | `String` | O | 인증된 전화번호 |

**처리**: 사용자 동의 정보에 전화번호 업데이트

---

## 발송 결과 처리

### 성공 시
- `NotificationHistory` 생성
- `status = SUCCESS`
- `sentAt` 기록

### 실패 시 (재시도 가능)
- `status = RETRYING`
- `retryCount` 증가
- `nextRetryAt` 설정 (Exponential Backoff: 1분, 5분, 15분)
- 스케줄러가 1분마다 재시도 대상 처리

### 최종 실패 시
- `status = DEAD_LETTER`
- 수동 재처리 대기

---

## Producer 연동 가이드

### Java/Spring 예시

```java
@Service
@RequiredArgsConstructor
public class NotificationClient {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendSms(List<Target> targets, String content) {
        SmsSendEvent event = new SmsSendEvent(
            NotificationType.MARKETING,
            targets.stream()
                .map(t -> new SmsSendEvent.Target(t.getUserId(), t.getPhoneNumber()))
                .toList(),
            content
        );

        kafkaTemplate.send("sms-send-request", objectMapper.writeValueAsString(event));
    }
}
```

---

**Last Updated**: 2025-12-12
