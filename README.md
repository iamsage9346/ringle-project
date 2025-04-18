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
## API 설계 의도 및 테스트 코드 설명

## 1. openAvailability()

### (1) 기능 설계 의도

`openAvailability()` 메소드는 튜터가 자신의 수업 가능 시간(availability)을 시스템에 등록하는 기능입니다. 해당 메소드는 다음과 같은 제약 조건을 바탕으로 동작합니다:

- 수업 시작 시간은 **정시 또는 반시(00분 / 30분)** 이어야 합니다.
- 수업 종료 시간은 **시작 시간으로부터 정확히 30분 후** 이어야 합니다.
- 존재하지 않는 튜터 ID에 대해서는 예외를 발생시킵니다.

이를 통해 **수업 시간 단위 표준화**와 **정확한 튜터 매칭**을 보장합니다.

---

### (2) 테스트 코드 설명

| 테스트 메소드                            | 설명                             |
| ---------------------------------- | ------------------------------ |
| `openAvailability_success`         | 정상 입력에 대한 저장 및 반환 검증           |
| `openAvailability_startTime_error` | 시작 시간이 00분/30분이 아닌 경우 예외 발생 확인 |
| `openAvailability_endTime_error`   | 수업이 30분이 아닐 경우 예외 발생 확인        |
| `openAvailability_notExistTutor`   | 존재하지 않는 튜터 ID일 경우 예외 발생 확인     |

---

### (3) API 설명

- **URL**: `POST /availability`
- **RequestBody**: `AvailabilityRequest`
  - `tutorId`: Long
  - `startTime`: LocalDateTime (정시 또는 반시만 가능)
  - `endTime`: LocalDateTime (startTime + 30분)

- **Response**: `200 OK`
  - `AvailabilityResponse`
    - `tutorId`: Long
    - `startTime`: LocalDateTime
    - `endTime`: LocalDateTime

---

## 2. DeleteAvailability()

### (1) 기능 설계 의도

`deleteAvailability()` 메소드는 튜터가 등록한 수업 가능 시간 중 특정 시간대를 삭제하는 기능입니다. 삭제는 다음 조건을 만족해야 합니다:

- 해당 시간에 등록된 수업 가능 시간이 존재해야 합니다.
- 존재하지 않을 경우 예외가 발생합니다.

이를 통해 **등록된 시간대에 한해서만 삭제 가능**하도록 합니다.

---

### (2) 테스트 코드 설명

| 테스트 메소드                        | 설명                                |
| ---------------------------------- | --------------------------------- |
| `deleteAvailability_success`       | 정상적인 시간대 삭제 동작 확인              |
| `deleteAvailability_fail_if_not_exist` | 삭제 대상이 존재하지 않는 경우 예외 발생 확인 |

---

### (3) API 설명

- **URL**: `DELETE /availability?tutorId={id}&startTime={start}`
- **Query Parameters**: tutorId, startTime
- **Response**: HTTP 204 No Content


---


## 3. searchByDateAvailableSlots()

### (1) 기능 설계 의도

`searchByDateAvailableSlots()` 메서드는 **학생이 특정 기간(`startDate ~ endDate`)과 수업 시간(`duration`)을 기준으로**  
**연속된 튜터 수업 가능 시간대**를 조회하는 기능입니다.  
연속된 시간대의 길이가 `duration`보다 길거나 같을 경우에만 결과로 반환됩니다.

이를 통해 사용자는 원하는 날짜와 시간 조건에 맞는 튜터의 수업 가능 슬롯을 효율적으로 조회할 수 있습니다.

---

### (2) 테스트 코드 설명

| 테스트 메소드                                       | 설명                                                               |
|----------------------------------------------------|--------------------------------------------------------------------|
| `searchByDateAvailableSlots_success`               | 30분 단위로 연속된 60분 가능 시간대가 존재할 경우 정상 조회 확인      |
| `searchByDateAvailableSlots_ignoreNonContinuousSlots` | 중간에 끊긴 시간대는 결과에 포함되지 않음을 검증                         |

---

### (3) API 설명

- **URL**: `GET /availability/search-by-date`
- **Query Parameters**:
  - `startDate` (ISO 8601 형식, 예: `2025-04-20`)
  - `endDate` (ISO 8601 형식)
  - `duration` (int, 수업 시간 단위: 분)
- **Response**: `200 OK`
  - `List<AvailabilitySlotByDateResponse>`
    - `studentId`: Long
    - `startTime`: LocalDateTime
    - `duration`: Enum (`MIN30` or `MIN60`)

---

## 4. searchByTimeAvailableSlots()

### (1) 기능 설계 의도

`searchByTimeAvailableSlots()` 메서드는 **학생이 특정 시작 시간(`startDateTime`)과 수업 시간(`duration`)을 기준으로** 해당 시점부터 **연속된 수업 가능 시간**이 존재하는 튜터들을 조회하는 기능입니다.

요청한 시간으로부터 `duration`만큼 연속된 슬롯이 존재하는 경우에만 해당 튜터의 정보를 결과에 포함합니다.

---

### (2) 테스트 코드 설명

| 테스트 메소드                                      | 설명                                                                  |
|---------------------------------------------------|-----------------------------------------------------------------------|
| `searchByTimeAvailableSlots_success`              | 특정 시작 시간 기준으로 60분 이상 연속된 가능 시간이 존재할 경우 정상 조회 확인     |
| `searchByTimeAvailableSlots_ignorePartialAvailability` | 중간에 끊긴 슬롯이 있을 경우 결과에서 제외됨을 확인                          |

---

### (3) API 설명

- **URL**: `GET /availability/search-by-time`
- **Query Parameters**:
  - `startDate` (ISO 8601 형식, 예: `2025-04-21T09:00:00`)
  - `duration` (int, 수업 시간 단위: 분)
- **Response**: `200 OK`
  - `List<AvailabilitySlotByTimeResponse>`
    - `tutorId`: Long
    - `tutorName`: String
    - `startTime`: LocalDateTime
    - `duration`: Enum (`MIN30` or `MIN60`)
---


## 5. createLesson()

### (1) 기능 설계 의도

`createLesson()` 메서드는 **학생이 특정 시간대, 수업 길이, 튜터 정보를 기반으로 새로운 수업을 신청하는 기능**입니다.  
요청된 수업 시간 동안 **튜터가 연속된 가용 시간 슬롯을 가지고 있는지**를 확인하고, 그 시간대가 예약되지 않았을 경우 수업을 생성합니다.

수업 길이는 `30분` 또는 `60분` 중 하나이며, 해당 시간만큼 연속된 `Availability`가 존재해야만 수업이 등록됩니다.

---

### (2) 테스트 코드 설명

| 테스트 메소드                            | 설명                                                                       |
|-----------------------------------------|----------------------------------------------------------------------------|
| `createLesson_success_30MIN`            | 30분 수업이 정상적으로 생성되는 경우를 검증                                     |
| `createLesson_success_60MIN`            | 60분 수업이 연속된 슬롯을 통해 정상 생성되는 경우를 검증                           |
| `createLesson_fail_ifStudentNotFound`   | 학생 정보가 존재하지 않을 경우 예외 발생 확인                                     |
| `createLesson_fail_ifTutorNotFound`     | 튜터 정보가 존재하지 않을 경우 예외 발생 확인                                     |
| `createLesson_fail_ifAvailabilityMissing` | 연속된 시간대가 존재하지 않을 경우(중간에 끊긴 경우) 예외 발생 확인                     |

---

### (3) API 설명

- **URL**: `POST /availability/new-lesson`
- **Request Body**: `LessonRequest`
  - `tutorId`: Long
  - `studentId`: Long
  - `startTime`: LocalDateTime
  - `lessonDuration`: Enum (`MIN30` or `MIN60`)
- **Response**: `200 OK`
  - `LessonResponse`
    - `studentId`: Long
    - `tutorId`: Long
    - `startTime`: LocalDateTime
    - `lessonDuration`: Enum (`MIN30` or `MIN60`)

---

## 6. getStudentLesson()

### (1) 기능 설계 의도

`getStudentLesson()` 메서드는 **특정 학생이 신청한 모든 수업 정보를 조회**하는 기능입니다.  
학생 ID를 기준으로, 해당 학생이 신청한 수업 리스트를 반환하며, 각 수업에 대해 튜터 이름, 수업 시간, 수업 길이 등의 정보를 포함합니다.

이를 통해 학생은 자신이 과거 혹은 미래에 예약한 수업 내역을 확인할 수 있습니다.

---

### (2) 테스트 코드 설명

| 테스트 메소드                  | 설명                                                             |
|-------------------------------|------------------------------------------------------------------|
| `getStudentLesson_success`    | 학생 ID로 수업 두 건을 정상적으로 조회하고, 튜터 이름 및 수업 시간 검증 |

---

### (3) API 설명

- **URL**: `GET /availability/my-lesson`
- **Query Parameters**:
  - `studentId`: Long
- **Response**: `200 OK`
  - `List<StudentLessonResponse>`
    - `tutorName`: String
    - `startTime`: LocalDateTime
    - `lessonDuration`: Enum (`MIN30`, `MIN60`)

---

## 7. RegisterIntegrationTest

### (1) 기능 설계 의도

`RegisterIntegrationTest`는 튜터의 수업 가능 시간 등록부터 학생의 수강 신청 및 수업 조회까지, 전체 흐름이 **정상적으로 동작하는지 통합적으로 검증**하는 테스트입니다.

- 100명의 튜터와 100명의 학생을 미리 생성합니다.
- 첫 번째 튜터는 2025년 4월 18일 09:00부터 30분 간격으로 10개의 수업 가능 시간(`Availability`)을 등록합니다.
- 첫 번째 학생은 해당 시간대에 대해 10개의 수업을 신청합니다.
- 마지막으로 `getStudentLesson()` 메서드를 호출해 정확히 10개의 수업이 등록되었는지 확인합니다.

---

### (2) 테스트 코드 설명

| 테스트 메소드              | 설명                                                             |
|---------------------------|------------------------------------------------------------------|
| `testStudentReservations` | 튜터의 수업 가능 시간 등록 → 학생 수업 예약 10건 등록 → 수업 조회로 10건 확인 |

---

### (3) 테스트 흐름 예시
```java
@BeforeEach
void setUp() {
  // 100명의 튜터 및 학생 생성
  // 첫 번째 튜터: 09:00~18:00 시간대 수업 가능 시간 10개 등록
}

@Test
void testStudentReservations() {
  // 첫 번째 학생이 튜터의 시간대에 대해 수업 10건 신청
  // 수업 조회 결과가 10건인지 확인
}
```

---



## 향후 고려 중인 구조 (비동기 메시지 처리)

현재는 동기 방식으로 수업 예약 API를 처리하고 있으나,  
실제 운영 환경에서는 여러 사용자가 동시에 동일한 시간대에 예약을 시도할 수 있음을 고려해  
**Kafka + Redis 기반의 비동기 이벤트 처리 구조**로 확장 가능한 설계를 고려 중입니다.

- Kafka: 수업 예약 요청을 큐에 비동기로 발행하여 병렬 처리 부담을 줄임
- Redis: 튜터의 특정 시간대에 대해 락을 걸어 중복 예약 방지

---

## 제출 정보
- 과제 제출일: 2025-04-18
- 제출자: 박상화


