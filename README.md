# Ringle 수강 신청 API

학생 (student)과 튜터 (tutor)의 1:1 수업하는 서비스에서, 수강 신청하는 API입니다.

---

## 실행 방법


### 환경 구성
- Java 17+
- Spring Boot 3.x
- Gradle
- MySQL 8.x
- JPA

### DB 설정 (`application.yml`)
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ringle
    username: [root]
    password: [root]
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
```

###  실행
```bash
./gradlew bootRun
```

---
## 설계 배경

- 서비스가 재시작되어도 상태 유지 → JPA 기반 MySQL 사용
- 30/60분 단위의 수업 → Availability를 30분 단위로 관리
- 60분 단위의 수업은 연속된 lesson을 가져야 하므로 N:M 관계 (Lesson ↔ Availability) → `LessonAvailability` 테이블로 연결
- 확장성을 고려한 단방향 연관관계 설계 (Student → Lesson, Tutor → Lesson)

---

## API 설계

### 1. 튜터 수업 가능 시간 등록/삭제

- **POST** `/create-availability`
  - 튜터가 수업 가능한 시간대를 등록
  - `RequestBody`: `tutorId`, `startTime`, `endTime`

- **DELETE** `/delete-availability/{tutorId}?startTime={startTime}`
  - 특정 시간대 삭제 (30분 단위)

---

### 2. 수업 가능 시간 조회

- **GET** `/search-by-date`
  - 특정 기간 동안 특정 수업 길이(30/60분) 가능한 시간 조회
  - `params`: `startDate`, `endDate`, `duration`

- **GET** `/search-by-time`
  - 특정 시작 시간에서 특정 수업 길이에 맞는 가능한 튜터 조회
  - `params`: `startDateTime`, `duration`

---

### 3. 수업 신청

- **POST** `/new-lesson`
  - 학생이 특정 시간, 튜터, 수업 길이로 수업 신청
  - `RequestBody`: `studentId`, `tutorId`, `startTime`, `lessonDuration`

---

### 4. 신청한 수업 조회

- **GET** `/my-lesson?studentId={id}`
  - 학생이 자신이 신청한 수업 목록 조회

---

## Test

### 단위 테스트
- Mockito 기반 단위 테스트 (`service` 계층)
- 테스트 실행:  
```bash
./gradlew test
```

---


## 제출 정보
- 과제 제출일: 2025-04-18
- 제출자: 박상화

