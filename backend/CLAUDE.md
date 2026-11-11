# backend

Spring Boot 4.1.2 · Java 21 · Gradle Kotlin DSL. 기본 패키지는 `com.eduhub`.

## 테스트 명령 (`backend/`에서)

- 전체: `./gradlew test`
- 클래스 하나: `./gradlew test --tests "com.eduhub.course.service.CourseAdminServiceTest"`
- 메서드 하나: `./gradlew test --tests "com.eduhub.course.service.CourseAdminServiceTest.메서드명"`
- 서비스: Mockito 단위 테스트. 컨트롤러: `@SpringBootTest` + `@AutoConfigureMockMvc` + `@Transactional`(테스트마다 롤백)
- 컨트롤러 테스트는 `local` 초기 데이터(`LocalDataInitializer`: 과정 3개, 관리자·임직원 계정)에 의존하고, 관리자 API는 `/api/auth/login`으로 받은 실제 토큰을 쓴다
- `SchemaMappingTest`: H2에서 Flyway를 돌리고 `validate`로 엔티티와 SQL의 일치를 검증한다 (Docker 불필요)
- 테스트 메서드 이름은 한글 문장 (`과정_목록은_로그인_없이_...`)

## 계층 규칙

- 도메인별 패키지 `user`, `course`, `enrollment`, 각각 `controller`/`service`/`repository`/`domain`/`dto`. 공통은 `global.config`, `global.security`, `global.error`, `global.common`, `global.init`
- 호출 방향은 controller → service → repository. `enrollment`가 `course`를 참조하므로 `course`는 `enrollment`를 참조하지 않는다
- 서비스 클래스에 `@Transactional(readOnly = true)`, 쓰기 메서드에만 `@Transactional`. 조회(`CourseService`)와 관리자 변경(`CourseAdminService`)을 나눈다
- 목록 API는 `Pageable`을 받아 `PageResponse.from(page)`로 반환 (기본 10, 최대 100)
- 오류는 `BusinessException(ErrorCode.X)`로 던지고, 새 오류는 `ErrorCode`에 추가한다(접두어: 공통 `G`, 인증 `A`, 과정·회차 `C`). `GlobalExceptionHandler`와 `SecurityConfig`가 `{code, message, timestamp}`로 응답한다

## JPA 규칙

- 컨트롤러는 엔티티 대신 `dto`를 반환한다. `open-in-view: false`이므로 지연 로딩 연관은 서비스(트랜잭션 안)에서 DTO로 바꾼다
- 연관관계는 `@ManyToOne(fetch = FetchType.LAZY)`, enum은 `@Enumerated(EnumType.STRING)`
- 엔티티: `protected` 기본 생성자, 정적 팩토리 `create(...)`, setter 없음. 상태 규칙(종료된 과정·회차 변경 불가, 시각 순서, 정원 ≥ 확정 인원)은 엔티티 메서드가 검사한다
- 스키마는 Flyway(`src/main/resources/db/migration`)로만 바꾼다. 모든 프로필에서 JPA는 `ddl-auto: validate` (D-20)
- 엔티티를 바꾸면 새 `V{n}__*.sql`을 추가하고 기존 마이그레이션은 고치지 않는다. SQL은 MySQL 8.4와 H2(MySQL 모드) 양쪽에서 실행돼야 한다(`ALTER TABLE`은 문장 하나에 변경 하나)
- `Instant`는 `preferred_instant_jdbc_type: TIMESTAMP`로 `DATETIME(6)`에 매핑된다

## 프로필

- `local`(기본): H2 메모리(MySQL 모드), 계정·`JWT_SECRET` 로컬 기본값 있음
- `dev`: docker-compose MySQL, root 계정(D-13). 루트 `.env`를 직접 읽음 (`MYSQL_*`, `JWT_SECRET`)
- `prod`: 비밀 값은 모두 환경 변수, Swagger 끔

## 도메인과 인증

- `Course`(분류·상태) 1:N `CourseSession`(일정·장소·정원·확정 인원) 1:N `Enrollment`. 수강 신청 단위는 회차 (D-17), (임직원, 회차) 유니크
- 남은 자리 = 정원 − `confirmedCount` (D-21). 신청·취소 로직은 이 값을 함께 바꾼다. 대기 순번은 WAITING 중 `appliedAt` 순서
- 역할은 `EMPLOYEE`, `ADMIN` (D-14). `POST /api/auth/login`이 HS256 JWT(`role` 클레임)를 발급하고 리소스 서버가 검증한다
- `SecurityConfig`: 로그인·Swagger·`GET /api/courses/**` 공개 (D-22), `/api/admin/**`은 ADMIN, 나머지는 인증 필요. 비밀번호는 BCrypt로만 저장

## OpenAPI 규칙

- springdoc 3.2.0. 문서 정보와 bearer 보안 스킴은 `global.config.OpenApiConfig`
- 컨트롤러에는 `@Tag`, 메서드에는 `@Operation(summary = …)`
- `prod`에서는 api-docs와 Swagger UI를 끈다 (D-07)
