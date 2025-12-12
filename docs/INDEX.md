# Notification Service - 문서 네비게이션 가이드

이 문서는 Notification Service 프로젝트의 모든 문서에 대한 네비게이션 가이드입니다.

---

## 문서 구조

```
docs/
├── INDEX.md                              # 문서 네비게이션 가이드 (현재 문서)
├── INFO.md                               # 프로젝트 개요 및 GitHub 자동화
├── ISSUE_GUIDE.md                        # 이슈 작성 가이드
├── PROJECT_SETUP.md                      # 프로젝트 설정 및 워크플로우
│
├── requirements/                         # 요구사항 문서
│   └── NOTIFICATION_REQUIREMENTS.md      # 비즈니스 요구사항 정의
│
└── architecture/                         # 아키텍처 문서
    ├── ARCHITECTURE_ANALYSIS.md          # 아키텍처 분석 및 결정
    └── DOMAIN_MODEL_DESIGN.md            # 도메인 모델 설계
```

---

## 문서 읽기 순서

### 처음 시작하는 경우

| 순서 | 문서                                                                        | 설명                       |
|----|---------------------------------------------------------------------------|--------------------------|
| 1  | [INFO.md](INFO.md)                                                        | 프로젝트 개요, 핵심 기능, 기술 스택 파악 |
| 2  | [NOTIFICATION_REQUIREMENTS.md](requirements/NOTIFICATION_REQUIREMENTS.md) | 비즈니스 요구사항 이해             |
| 3  | [ARCHITECTURE_ANALYSIS.md](architecture/ARCHITECTURE_ANALYSIS.md)         | 아키텍처 설계 결정 이해            |
| 4  | [DOMAIN_MODEL_DESIGN.md](architecture/DOMAIN_MODEL_DESIGN.md)             | 도메인 모델 구조 파악             |
| 5  | [PROJECT_SETUP.md](PROJECT_SETUP.md)                                      | 개발 워크플로우 숙지              |

### 역할별 필수 문서

| 역할              | 필수 문서                                                                                                         |
|-----------------|---------------------------------------------------------------------------------------------------------------|
| **Backend 개발자** | INFO.md > NOTIFICATION_REQUIREMENTS.md > ARCHITECTURE_ANALYSIS.md > DOMAIN_MODEL_DESIGN.md > PROJECT_SETUP.md |
| **PM/기획자**      | INFO.md > NOTIFICATION_REQUIREMENTS.md                                                                        |
| **QA**          | INFO.md > NOTIFICATION_REQUIREMENTS.md > PROJECT_SETUP.md                                                     |
| **DevOps**      | INFO.md > PROJECT_SETUP.md                                                                                    |
| **아키텍트**        | 전체 문서                                                                                                         |

---

## 문서 카테고리별 가이드

### 프로젝트 개요

| 문서      | 위치                      | 설명                                |
|---------|-------------------------|-----------------------------------|
| INFO.md | [docs/INFO.md](INFO.md) | 프로젝트 개요, 핵심 기능, 기술 스택, GitHub 자동화 |

**읽어야 할 때:**

- 프로젝트에 처음 참여할 때
- 전체 기능 범위를 파악할 때
- 기술 스택을 확인할 때

---

### 요구사항 문서

| 문서                           | 위치                                                              | 설명           |
|------------------------------|-----------------------------------------------------------------|--------------|
| NOTIFICATION_REQUIREMENTS.md | [docs/requirements/](requirements/NOTIFICATION_REQUIREMENTS.md) | 비즈니스 요구사항 정의 |

**주요 내용:**

- 알림 채널 (이메일, SMS, 앱 푸시, 인앱 알림)
- 알림 유형 (트랜잭션, 서비스, 광고/마케팅)
- 수신 동의 관리 규칙
- 야간 발송 제한 정책
- 외부 연동 사양 (Solapi, FCM, Gmail SMTP)
- 발송 이력 관리 요구사항

**읽어야 할 때:**

- 기능 구현 전 요구사항 확인
- 테스트 케이스 작성 시
- 요구사항 변경 검토 시

---

### 아키텍처 문서

| 문서                       | 위치                                                          | 설명              |
|--------------------------|-------------------------------------------------------------|-----------------|
| ARCHITECTURE_ANALYSIS.md | [docs/architecture/](architecture/ARCHITECTURE_ANALYSIS.md) | 아키텍처 패턴 분석 및 결정 |
| DOMAIN_MODEL_DESIGN.md   | [docs/architecture/](architecture/DOMAIN_MODEL_DESIGN.md)   | 도메인 모델 설계       |

**ARCHITECTURE_ANALYSIS.md 주요 내용:**

- Hexagonal Architecture 적용 결정
- 동기/비동기 처리 구조
- 채널별 Adapter 설계
- 재시도 정책
- 인앱 알림 전달 방식

**DOMAIN_MODEL_DESIGN.md 주요 내용:**

- Aggregate 구조
- Value Objects 정의
- 비즈니스 규칙
- Entity 관계

**읽어야 할 때:**

- 새로운 기능 설계 시
- 코드 구조 이해가 필요할 때
- 아키텍처 리뷰 시

---

### 프로젝트 관리 문서

| 문서               | 위치                                        | 설명                           |
|------------------|-------------------------------------------|------------------------------|
| PROJECT_SETUP.md | [docs/PROJECT_SETUP.md](PROJECT_SETUP.md) | 프로젝트 설정, 워크플로우, AI 어시스턴트 가이드 |
| ISSUE_GUIDE.md   | [docs/ISSUE_GUIDE.md](ISSUE_GUIDE.md)     | 이슈 타입별 작성 가이드                |

**읽어야 할 때:**

- 이슈/PR 생성 시
- 개발 워크플로우 확인 시
- Kafka 토픽/이벤트 스키마 확인 시

---

## 빠른 참조

### 핵심 정보

| 항목        | 내용                                        | 참조 문서                                                             |
|-----------|-------------------------------------------|-------------------------------------------------------------------|
| **알림 채널** | 이메일, SMS, 앱 푸시, 인앱 알림                     | [INFO.md](INFO.md)                                                |
| **외부 연동** | Gmail SMTP, Solapi, FCM                   | [INFO.md](INFO.md)                                                |
| **기술 스택** | Spring Boot 3.x, PostgreSQL, Redis, Kafka | [INFO.md](INFO.md)                                                |
| **아키텍처**  | Hexagonal + DDD + Event-Driven            | [ARCHITECTURE_ANALYSIS.md](architecture/ARCHITECTURE_ANALYSIS.md) |

### 핵심 비즈니스 규칙

| 규칙            | 설명                             |
|---------------|--------------------------------|
| **트랜잭션 알림**   | 수신 동의 불필요, 즉시 발송               |
| **서비스 알림**    | 선택적 수신 동의, 야간 발송 허용            |
| **광고/마케팅 알림** | 수신 동의 필수, 야간(21시~08시) 동의 시만 발송 |

### 기존 시스템 호환

| 항목           | 값                                   |
|--------------|-------------------------------------|
| **Kafka 토픽** | `email-confirm-request`             |
| **이벤트 스키마**  | `EmailConfirmEvent { email, code }` |

---

## 문서 업데이트 규칙

### 요구사항 변경 시

1. `NOTIFICATION_REQUIREMENTS.md` 업데이트
2. 영향받는 아키텍처 문서 검토 및 수정
3. Change Request 이슈 생성

### 아키텍처 변경 시

1. `ARCHITECTURE_ANALYSIS.md` 또는 `DOMAIN_MODEL_DESIGN.md` 업데이트
2. ADR(Architecture Decision Record) 작성 고려
3. 관련 이슈에 문서 링크 추가

### 신규 기능 추가 시

1. 해당하는 문서 섹션 업데이트
2. 필요 시 feature 폴더에 상세 문서 추가

---

## 문서 검색 팁

### 특정 개념 찾기

| 찾고 싶은 것  | 확인할 문서                                  |
|----------|-----------------------------------------|
| 알림 채널 종류 | INFO.md > 핵심 기능 > 알림 채널                 |
| 수신 동의 규칙 | NOTIFICATION_REQUIREMENTS.md > 수신 동의 관리 |
| Kafka 토픽 | PROJECT_SETUP.md > Kafka 토픽 및 이벤트 스키마   |
| 도메인 모델   | DOMAIN_MODEL_DESIGN.md                  |
| 아키텍처 패턴  | ARCHITECTURE_ANALYSIS.md                |

---

**Last Updated**: 2025-12-11
