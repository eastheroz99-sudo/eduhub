# EduHub

사내 교육과정 조회·수강 신청 시스템. 요구사항과 결정 사항은 [docs/requirements.md](docs/requirements.md)를 참고하세요.

## 폴더 구조

```
eduhub/
├── backend/            # Spring Boot 4.1.2 / Java 21 (Gradle Kotlin DSL)
├── docs/               # 요구사항·결정 사항
├── docker-compose.yml  # 개발용 MySQL 8.4 (dev 프로필)
├── .env.example        # 환경 변수 예시 (복사해서 .env로 사용)
├── .gitignore
└── README.md
```

## 시작하기

준비물: JDK 21 (dev 프로필은 Docker도 필요)

1. 백엔드를 실행합니다. 기본 프로필은 `local`(H2 메모리 DB, 샘플 계정·과정 자동 생성)입니다.

   ```bash
   cd backend
   ./gradlew bootRun
   ```

2. API 문서: http://localhost:8080/swagger-ui.html (`local`에서만)

3. 관리자 API를 쓰려면 `POST /api/auth/login`으로 토큰을 받아 Swagger의 Authorize에 넣습니다.
   local 기본 계정: `admin@eduhub.local` / `local-admin-only` (관리자), `employee@eduhub.local` / `local-employee-only` (임직원)

테스트: `cd backend && ./gradlew test`

### dev 프로필 (MySQL)

1. `.env.example`을 복사해 `.env`를 만들고 `MYSQL_ROOT_PASSWORD`, `JWT_SECRET`(32바이트 이상)을 채웁니다.

   ```bash
   cp .env.example .env
   ```

2. MySQL을 띄우고 dev 프로필로 실행합니다. 스키마는 Flyway가 만듭니다.

   ```bash
   docker compose up -d
   cd backend
   ./gradlew bootRun --args='--spring.profiles.active=dev'
   ```

> `.env`에는 비밀 값이 들어가므로 Git에 올리지 않습니다 (`.gitignore`에 포함).
