# 바로잇 Backend

멘토-멘티 학습 매칭 플랫폼 **바로잇**의 백엔드 서버입니다.
멘토가 과제를 내고 피드백을 작성하고, 멘티는 학습 자료를 보면서 과제를 제출하고 일일 학습을 관리하는 흐름을 지원합니다.

![Kotlin](https://img.shields.io/badge/Kotlin-2.2-7F52FF?logo=kotlin&logoColor=white)
![Java](https://img.shields.io/badge/Java-21-007396?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0-6DB33F?logo=springboot&logoColor=white)
![JPA](https://img.shields.io/badge/JPA-Hibernate-59666C)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?logo=redis&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?logo=docker&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-2088FF?logo=githubactions&logoColor=white)


<br>

## 팀 구성 및 역할 분담

<table>
<tr>
<td align="center" width="33%">
<a href="https://github.com/JaeYunChung"><img src="https://github.com/JaeYunChung.png" width="180" /></a><br/>
<b>정재윤</b><br/>
<a href="https://github.com/JaeYunChung">@JaeYunChung</a>
</td>
<td align="center" width="33%">
<a href="https://github.com/yechan-kim"><img src="https://github.com/yechan-kim.png" width="180" /></a><br/>
<b>김예찬</b><br/>
<a href="https://github.com/yechan-kim">@yechan-kim</a>
</td>
<td align="center" width="33%">
<a href="https://github.com/heejeongJ"><img src="https://github.com/heejeongJ.png" width="180" /></a><br/>
<b>주희정</b><br/>
<a href="https://github.com/heejeongJ">@heejeongJ</a>
</td>
</tr>
<tr valign="top">
<td>

- 인증 / 회원 (JWT · 로그인)
- 멘티 정보 / 메인 페이지 / 캘린더
- 댓글 · 대댓글(Comment / SubComment)
- 멘토 대시보드 API

</td>
<td>

- 할 일(ToDo) + 학습 시간 캘린더
- 과제 할당 / 멘티 타임테이블
- 알림(Notification) 발송 — SSE 이벤트
- 종합 평가(Overall) · 배지(Badge) 시스템

</td>
<td>

- 과제(Assignment) · 과제 템플릿
- 피드백(Feedback) · 피드백 템플릿
- 학습 자료(LearningResource)
- 파일 업로드 + Oracle OCI ObjectStorage

</td>
</tr>
</table>


<br><br>
## 주요 기능

### 인증 / 회원
- JWT 기반 로그인 + Refresh 토큰
- 멘토 / 멘티 역할 분리
- 접속 로그(`AccessLog`) 기록

### 학습 워크플로우
- 멘토: **과제 템플릿** 작성 → 멘티 과제 할당
- 멘티: 과제 제출 (파일 업로드)
- 멘토: **피드백** 작성 + 일일 요약 → 멘티에 SSE 알림 발송
- **학습 자료** 등록 / 과제 템플릿에 자료 연동
- **피드백 템플릿** 으로 자주 쓰는 문구 재사용

### 멘티 학습 도구
- 일일 할 일(ToDo) 관리 + 학습 시간 캘린더
- 일일 피드백 요약 / 멘티 메인 페이지 집계
- 과제 진행 상태(`SUBMITTED → FEEDBACKED`) 추적
- 종합 평가(`Overall`), 배지(`Badge`) 시스템

### 커뮤니케이션
- **SSE 실시간 알림** (피드백 도착, 과제 마감 등)
- 댓글(`Comment`)
- 종합 평가 / 총평 작성

### 운영
- 관리자 도메인(`Admin`)
- 에러 로그 수집(`ErrorLog`) — 발생한 모든 `ServiceException` 자동 적재
- Oracle OCI ObjectStorage **PreAuthenticated URL** 발급으로 안전한 파일 업로드 / 다운로드

<br><br>

## 기술 스택

| 분야 | 기술 |
|---|---|
| 언어 / 런타임 | Kotlin 2.2.21, Java 21 (Amazon Corretto) |
| 프레임워크 | Spring Boot 4.0.2, Spring Security |
| ORM | Spring Data JPA + Hibernate (`open-in-view: false`, JPA Auditing) |
| 데이터베이스 | MySQL 8.0 (UTF-8 unicode_ci), Redis |
| 인증 | JWT (jjwt 0.13) — Access / Refresh 분리 |
| 테스트 | Kotest 6.1 (DescribeSpec), MockK 1.14 |
| 클라우드 스토리지 | Oracle OCI ObjectStorage (PreAuthenticated URL) |
| 실시간 통신 | Server-Sent Events (SSE) |
| API 문서 | Swagger / OpenAPI (springdoc) |
| 빌드 | Gradle (Kotlin DSL) |
| 컨테이너 | Docker (linux/amd64, linux/arm64 multi-arch) |
| CI / CD | GitHub Actions + Docker Hub + SSH 배포 |
| 로깅 | kotlin-logging (`io.github.oshai`) |
| 타임존 | Asia/Seoul (Dockerfile + MySQL + JPA) |

<br><br>

## 아키텍처

### 1. CQRS 적용 — 자체 어노테이션으로 Command / Query 분리

`@Service` 대신 명령 / 조회 의도를 드러내는 어노테이션을 정의하여 트랜잭션 readOnly 여부까지 어노테이션 레벨에서 강제합니다.

```kotlin
// global/annotation/CommandUseCase.kt
@Service
@Transactional
annotation class CommandUseCase(...)

// global/annotation/QueryUseCase.kt
@Service
@Transactional(readOnly = true)
annotation class QueryUseCase(...)
```

- 조회 전용 유스케이스는 의도하지 않은 부수 효과를 어노테이션 레벨에서 차단
- 클래스 이름만으로 책임 식별 가능 (`FeedbackCreateUseCase`, `FeedbackListQueryUseCase` ...)
- 하나의 도메인 안에서도 명령 / 조회가 분리되어 변경 영향 범위가 작음

<br>

### 2. 도메인별 패키지 + UseCase 단위 분리

거대한 `Service` 클래스 대신 **유스케이스 한 건 = 클래스 한 개** 원칙을 적용했습니다.

```
domain/feedback/
├── controller/
│   ├── FeedbackApi.kt          ← Swagger 인터페이스
│   └── FeedbackController.kt   ← 구현
├── dto/
│   ├── request/
│   └── response/
├── entity/
│   ├── Feedback.kt
│   └── enums/
├── repository/
└── usecase/
    ├── FeedbackCreateUseCase.kt
    ├── FeedbackDetailQueryUseCase.kt
    ├── FeedbackListQueryUseCase.kt
    ├── DailyFeedbackQueryUseCase.kt
    └── DailyFeedbackSummaryQueryUseCase.kt
```

- 단일 책임(SRP) → 변경 / 테스트 영향 범위 작음
- 새 기능 추가 시 기존 클래스 수정 대신 새 UseCase 추가
- Swagger 인터페이스 (`*Api.kt`)와 구현 (`*Controller.kt`) 분리로 API 문서가 컨트롤러에 묻히지 않음

<br>

### 3. JPA Auditing + BaseEntity

```kotlin
@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,
    @CreatedDate @Column(updatable = false) var createdAt: LocalDateTime? = null,
    @LastModifiedDate @Column var updatedAt: LocalDateTime? = null,
)
```

- 모든 엔티티가 `id` / `createdAt` / `updatedAt` 자동 관리
- `open-in-view: false` 로 트랜잭션 경계 명확화 (LazyLoading 누수 차단)

<br>

### 4. SSE 실시간 알림 인프라

`global/sse/` 에 알림 인프라가 도메인과 분리되어 있습니다.

```
global/sse/
├── controller/        ← /sse/subscribe API
├── dto/               ← SseEvent base + 응답 DTO
├── event/             ← SseEventPublisher / SseEventListener
├── repository/        ← SseEmitterRepository (연결 관리)
└── usecase/
    ├── SseSubscribeUseCase.kt
    ├── SsePublishUseCase.kt
    ├── SseHeartbeatUseCase.kt   ← 주기적 ping
    └── SseQueryUseCase.kt
```

- `SseShutdownHandler` 로 종료 시 활성 연결 정리
- 알림 이벤트는 도메인이 직접 SSE 객체를 다루지 않고 publisher 를 통해 발행 (도메인-인프라 분리)

<br>

### 5. Kotest + MockK 기반 BDD 테스트

```kotlin
class FeedbackListQueryUseCaseTest : DescribeSpec({
    describe("getListByMentor") {
        it("멘토 기준 제출된 과제가 없으면 빈 리스트를 반환한다") { ... }
        it("멘토 기준 피드백 목록을 반환한다") { ... }
    }
})
```

- 도메인 유스케이스 단위 테스트 (`src/test/kotlin/.../usecase/`)
- fixture 함수 + MockK relaxed mock 으로 테스트 가독성 확보
- PR 시 `./gradlew test` 자동 실행 (workflow `test.yaml`) — 실패 시 머지 차단

<br>

### 6. CI / CD 파이프라인

```
[ PR ]                   [ push to develop|main ]                [ CI 성공 ]
   │                              │                                  │
   ▼                              ▼                                  ▼
test.yaml                     ci.yaml                            cd.yaml
(./gradlew test)         build → multi-arch image → Docker Hub      SSH 배포
```

- **test**: PR 단위 테스트
- **CI**: develop / main 푸시 시 multi-arch (amd64 / arm64) 도커 이미지 빌드 + 푸시
- **CD**: CI 성공 시 SSH 로 원격 서버에서 `deploy.sh` 실행
- **Profile 분리**: 도커 컨테이너 실행 시 `-Dspring.profiles.active=${PROFILE}` 로 환경 분기

<br><br>
## 도메인 구조

```
src/main/kotlin/com/barostartbe/
├── domain/
│   ├── auth/                ← 로그인 / JWT 발급
│   ├── user/                ← 공통 사용자 + 접속 로그
│   ├── mentor/              ← 멘토 정보
│   ├── mentee/              ← 멘티 정보 + 학습 캘린더
│   ├── assignment/          ← 과제 (제출 / 상태 전이)
│   ├── assignmenttemplate/  ← 과제 템플릿 (재사용)
│   ├── feedback/            ← 피드백 작성 / 조회
│   ├── feedbacktemplate/    ← 피드백 템플릿
│   ├── learningresource/    ← 학습 자료
│   ├── todo/                ← 일일 할 일
│   ├── comment/             ← 댓글
│   ├── notification/        ← 알림 도메인
│   ├── overall/             ← 종합 평가 / 총평
│   ├── badge/               ← 배지
│   ├── file/                ← 파일 메타
│   ├── objectstorage/       ← Oracle OCI 연동
│   ├── admin/               ← 관리자
│   └── errorlog/            ← 에러 로그
└── global/
    ├── annotation/          ← @CommandUseCase, @QueryUseCase
    ├── common/              ← BaseEntity, BaseFileEntity
    ├── config/              ← Cors / Oci / Redis / Security / SseShutdown / Web 설정
    ├── error/               ← ServiceException
    ├── handler/             ← GlobalExceptionHandler
    ├── response/            ← ApiResponse + Success/ErrorCode
    ├── security/
    │   ├── form/            ← Form 로그인 4종 컴포넌트
    │   └── jwt/             ← JWT 필터 + Util + TokenType
    ├── sse/                 ← SSE 인프라 일체
    └── swagger/             ← OpenAPI 설정
```

<br><br>

## 시작하기

### 사전 요구

- JDK 21 (Amazon Corretto 권장)
- Docker / Docker Compose
- Oracle OCI 계정 (파일 업로드 기능 사용 시)

### 로컬 실행

```bash
# 1. MySQL 컨테이너 실행
docker compose -f docker-compose-local.yml up -d

# 2. 환경 변수 설정 (.env 또는 IDE Run Configuration)
#    DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD
#    REDIS_HOST, REDIS_PORT, SSL_ENABLED
#    JWT_SECRET, JWT_ISSUER, JWT_TOKEN_EXPIRE, JWT_REFRESH_EXPIRE
#    OCI_TENANT_ID, OCI_USER_ID, OCI_FINGERPRINT, OCI_PRIVATE_KEY_PATH,
#    OCI_REGION, OCI_BUCKET_NAME, OCI_BUCKET_NAMESPACE
#    APP_SERVER_URL, APP_SERVICE_URL

# 3. 빌드 + 실행
./gradlew clean build
./gradlew bootRun
```

서버 기동 후 Swagger UI: `http://localhost:8080/swagger-ui/index.html`
<br><br>
### 테스트 실행

```bash
./gradlew test
```

<br><br>

## 브랜치 전략

- `main` — 운영 배포
- `develop` — 통합 브랜치
- `feat/#이슈번호`, `fix/#이슈번호`, `refactor/#이슈번호` ... — 기능별 단기 브랜치
