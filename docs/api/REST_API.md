# Notification Service - REST API 명세

> **Version**: 1.0.0
> **Base URL**: `/api/v1`

---

## 목차

- [개요](#개요)
- [공통 응답 형식](#공통-응답-형식)
- [SMS API](#sms-api)
- [Email API](#email-api)
- [Push API](#push-api)
- [Kakao 알림톡 API](#kakao-알림톡-api)
- [동의 관리 API](#동의-관리-api)

---

## 개요

### 알림 타입

| 타입 | 설명 | 동의 확인 |
|------|------|----------|
| `TRANSACTIONAL` | 트랜잭션 알림 (인증번호, 주문확인 등) | 불필요 |
| `MARKETING` | 마케팅 알림 (프로모션, 이벤트 등) | 필수 |
| `SERVICE` | 서비스 알림 (예약 리마인드 등) | 선택적 |

### 발송 대상 구조

모든 발송 API는 `targets` 배열을 통해 단건/다건 발송을 지원합니다.

```json
{
  "type": "TRANSACTIONAL",
  "targets": [
    { "userId": "user-1", "recipient": "..." },
    { "userId": "user-2", "recipient": "..." }
  ],
  "content": "..."
}
```

---

## 공통 응답 형식

### 성공 응답

```json
{
  "notificationIds": [123456789, 123456790],
  "totalCount": 2,
  "successCount": 2,
  "status": "ACCEPTED"
}
```

| 필드 | 타입 | 설명 |
|------|------|------|
| `notificationIds` | `Long[]` | 생성된 알림 이력 ID 목록 |
| `totalCount` | `int` | 총 발송 대상 수 |
| `successCount` | `int` | 요청 접수된 수 |
| `status` | `String` | 요청 상태 (`ACCEPTED`) |

### 에러 응답

```json
{
  "timestamp": "2025-12-12T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "전화번호는 필수입니다",
  "path": "/api/v1/sms/send"
}
```

---

## SMS API

### SMS 발송

단건 또는 다건 SMS 발송 요청

**Endpoint**: `POST /api/v1/sms/send`

**Request Body**:

```json
{
  "type": "TRANSACTIONAL",
  "targets": [
    {
      "userId": "user-123",
      "phoneNumber": "01012345678"
    }
  ],
  "content": "[BANDER] 인증번호는 [123456] 입니다."
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `type` | `String` | O | 알림 타입 (`TRANSACTIONAL`, `MARKETING`, `SERVICE`) |
| `targets` | `Array` | O | 발송 대상 목록 |
| `targets[].userId` | `String` | O | 사용자 ID |
| `targets[].phoneNumber` | `String` | O | 전화번호 (01012345678 형식) |
| `content` | `String` | O | 메시지 내용 (90byte 초과 시 LMS로 자동 전환) |

**Response**:

```json
{
  "notificationIds": [123456789],
  "totalCount": 1,
  "successCount": 1,
  "status": "ACCEPTED"
}
```

**동의 확인**:
- `MARKETING` 타입인 경우: `marketingConsent=true AND smsConsent=true` 확인
- 동의 없으면 발송 실패 처리 (이력에 `CONSENT_NOT_GRANTED` 기록)

---

## Email API

### 이메일 발송

단건 또는 다건 이메일 발송 요청

**Endpoint**: `POST /api/v1/email/send`

**Request Body**:

```json
{
  "type": "MARKETING",
  "targets": [
    {
      "userId": "user-123",
      "email": "user@example.com"
    }
  ],
  "title": "이번 주 특가 프로모션",
  "content": "<html><body><h1>50% 할인</h1></body></html>"
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `type` | `String` | O | 알림 타입 |
| `targets` | `Array` | O | 발송 대상 목록 |
| `targets[].userId` | `String` | O | 사용자 ID |
| `targets[].email` | `String` | O | 이메일 주소 |
| `title` | `String` | O | 이메일 제목 |
| `content` | `String` | O | 이메일 내용 (HTML 지원) |

**Response**:

```json
{
  "notificationIds": [123456789],
  "totalCount": 1,
  "successCount": 1,
  "status": "ACCEPTED"
}
```

**동의 확인**:
- `MARKETING` 타입인 경우: `marketingConsent=true AND emailConsent=true` 확인

---

## Push API

### 푸시 알림 발송

단건 또는 다건 FCM 푸시 발송 요청

**Endpoint**: `POST /api/v1/push/send`

**Request Body**:

```json
{
  "type": "TRANSACTIONAL",
  "targets": [
    {
      "userId": "user-123",
      "deviceToken": "fcm_device_token_xxxxx"
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
| `title` | `String` | O | 알림 제목 |
| `body` | `String` | O | 알림 본문 |
| `data` | `Object` | X | 추가 데이터 (앱에서 처리용) |

**Response**:

```json
{
  "notificationIds": [123456789],
  "totalCount": 1,
  "successCount": 1,
  "status": "ACCEPTED"
}
```

---

## Kakao 알림톡 API

### 알림톡 발송

단건 또는 다건 카카오 알림톡 발송 요청

**Endpoint**: `POST /api/v1/kakao/send`

**Request Body**:

```json
{
  "type": "TRANSACTIONAL",
  "targets": [
    {
      "userId": "user-123",
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
| `targets[].phoneNumber` | `String` | O | 카카오톡 연동 전화번호 |
| `templateCode` | `String` | O | 카카오 알림톡 템플릿 코드 |
| `variables` | `Object` | X | 템플릿 변수 |

**Response**:

```json
{
  "notificationIds": [123456789],
  "totalCount": 1,
  "successCount": 1,
  "status": "ACCEPTED"
}
```

**동의 확인**:
- `MARKETING` 타입인 경우: `marketingConsent=true AND kakaoConsent=true` 확인

---

## 동의 관리 API

### 사용자 동의 정보 조회

**Endpoint**: `GET /api/v1/consents/{userId}`

**Response**:

```json
{
  "userId": "user-123",
  "serviceConsent": true,
  "marketingConsent": true,
  "nightAdConsent": false,
  "smsConsent": true,
  "emailConsent": true,
  "kakaoConsent": false,
  "phoneNumber": "01012345678",
  "createdAt": "2025-01-01T10:00:00",
  "updatedAt": "2025-12-01T15:30:00"
}
```

### 채널별 동의 변경

**Endpoint**: `PUT /api/v1/consents/{userId}/{channel}`

- `PUT /api/v1/consents/{userId}/sms` - SMS 동의 변경
- `PUT /api/v1/consents/{userId}/email` - 이메일 동의 변경
- `PUT /api/v1/consents/{userId}/kakao` - 카카오 동의 변경
- `PUT /api/v1/consents/{userId}/night-ad` - 야간 광고 동의 변경

**Request Body**:

```json
{
  "consented": true
}
```

**Response**: 변경된 전체 동의 정보 반환

---

## 에러 코드

| HTTP Status | 코드 | 설명 |
|-------------|------|------|
| 400 | Bad Request | 요청 파라미터 오류 |
| 404 | Not Found | 사용자/리소스 없음 |
| 500 | Internal Server Error | 서버 오류 |

---

## 발송 상태

| 상태 | 설명 |
|------|------|
| `PENDING` | 발송 대기 |
| `SUCCESS` | 발송 성공 |
| `FAILED` | 발송 실패 (재시도 불가) |
| `RETRYING` | 재시도 중 |
| `DEAD_LETTER` | 최종 실패 (재시도 횟수 초과) |

---

**Last Updated**: 2025-12-12
