# 🎯 PT Schedule - PT 일정 관리 시스템

PT Schedule은 트레이너와 회원을 위한 종합 일정 관리 시스템으로, 효율적인 PT 일정 관리와 실시간 알림에 중점을 둔 솔루션입니다. 트레이너와 회원 간의 PT 일정을 체계적으로 관리하고, FCM 기반 알림 기능을 통해 일정 변경사항을 실시간으로 전달합니다.

## 📂 주요 사용자

- **회원**: PT 일정 예약, 변경 및 취소, 알림 수신을 수행하는 사용자
- **트레이너**: 회원 관리, PT 일정 관리, 불참 처리 등을 담당하는 트레이너

## 🚀 프로젝트 핵심 목표

- 효율적인 PT 일정 관리 시스템 구축
- 실시간 알림을 통한 일정 변경 사항 전달
- 회원과 트레이너 간 원활한 일정 조율 지원
- PT 계약 및 회차 관리 자동화

## 📊 프로젝트 구조

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── example/
│   │           └── final_project_be/
│   │               ├── config/             # 설정 클래스
│   │               ├── domain/             # 도메인별 패키지
│   │               │   ├── member/         # 회원 관련 클래스
│   │               │   ├── trainer/        # 트레이너 관련 클래스
│   │               │   ├── pt/             # PT 스케줄 관련 클래스
│   │               │   └── schedule/       # 일정 관련 클래스
│   │               ├── security/           # 보안 관련 클래스
│   │               └── util/               # 유틸리티 클래스
│   └── resources/
│       ├── application.yml                 # 애플리케이션 설정
│       └── static/                         # 정적 리소스
└── test/                                   # 테스트 코드
```

## 📋 주요 기능

### 회원 기능

- 회원가입 및 로그인
- PT 일정 조회 및 예약
- PT 일정 변경 및 취소
- 실시간 알림 수신

### 트레이너 기능

- 트레이너 가입 및 로그인
- 회원별 PT 일정 관리
- 불참 처리 및 일정 변경
- 실시간 알림 전송

### PT 일정 관리

- PT 일정 생성 및 조회
- 일정 변경 및 취소
- 불참 처리
- 회원별 남은 PT 횟수 관리

### 알림 시스템

- FCM(Firebase Cloud Messaging) 기반 실시간 알림
- 일정 변경/취소 시 자동 알림
- 당일/다음날 PT 일정 알림

## 🔑 KEY SUMMARY

### 1. 실시간 알림 시스템

- Firebase Cloud Messaging 푸시 알림
- PT 일정 변경/취소/추가 시 자동 알림
- 예정된 PT 일정 리마인더

### 2. 인증/보안

- JWT 기반 토큰 관리
- Spring Security 기반 인증/인가
- Role 기반 접근 제어 (MEMBER, TRAINER)

### 3. 일정 관리

- 트레이너 가용 시간 자동 체크
- PT 회차 자동 관리
- PT 계약 기반 횟수 관리

## 🔧 기술적 고도화

### 1. 실시간 알림 처리

- FCM을 통한 효율적인 푸시 알림 전송
- 알림 로그 관리 및 전송 상태 추적

### 2. 일정 관리 최적화

- 트레이너 가용 시간과 기존 예약 자동 검증
- PT 회차 자동 재계산 로직 구현

### 3. 보안 강화

- JWT + Spring Security 기반 인증
- Role 기반 API 접근 제어

## 🚨 트러블 슈팅

### 1. PT 일정 충돌 관리

- 문제: 트레이너 일정 중복 발생
- 해결:
  - 트레이너 가용 시간 및 불가능 시간 설정 기능 도입
  - 예약 전 일정 충돌 자동 검증 로직 구현

### 2. PT 횟수 관리

- 문제: 일정 변경/취소 시 PT 횟수 불일치
- 해결:
  - 일정 상태에 따른 자동 횟수 조정
  - 계약별 PT 회차 재계산 로직 구현

## 🛠 기술 스택

### 언어 및 프레임워크

- Java 17
- Spring Boot 3.x

### 데이터베이스

- PostSQL: 주요 관계형 데이터 저장

### 인증/보안

- Spring Security
- JWT Authentication
- BCrypt 비밀번호 암호화

### 알림

- Firebase Cloud Messaging (FCM)

### 기타

- Spring Data JPA
- QueryDSL (타입 안전 쿼리)

## 📚 API 문서

- Swagger UI를 통한 API 문서화
- `/swagger-ui/index.html`에서 확인 가능

## 🚀 시작하기

### 필수 요구사항

- Java 17 이상
- MySQL 8.0 이상
- Maven

### 설치 및 실행

1. 프로젝트 클론

```bash
git clone [repository-url]
```

2. 데이터베이스 설정

- MySQL 데이터베이스 생성
- `application.properties` 또는 `application.yml`에서 데이터베이스 연결 정보 설정

3. 프로젝트 빌드

```bash
mvn clean install
```

4. 애플리케이션 실행

```bash
mvn spring-boot:run
```

## 📜 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다.
