# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 프로젝트

EduHub: 사내 교육과정 조회·수강 신청 시스템 (정원 초과 시 대기자 등록). 요구사항(FR·NFR)과 결정 사항(D-xx)의 기준은 `docs/requirements.md`다.
진행 상태 (D-19): 백엔드는 FR-01~03까지 구현, 수강 신청·대기·취소(FR-04~06)는 3주차, 프론트엔드는 아직 없다.

## 디렉터리 지도

- `backend/`: Spring Boot 4.1.2 / Java 21 API 서버 (Gradle Kotlin DSL). 규칙은 `backend/CLAUDE.md`
- `frontend/`: React 18 + TypeScript MFE (아직 코드 없음). 규칙은 `frontend/CLAUDE.md`
- `docs/requirements.md`: 개요, 기술 스택, FR·NFR, 결정 사항
- `docker-compose.yml`: dev 프로필용 MySQL 8.4
- `.env.example`: 환경 변수 예시 (`.env`로 복사해 사용)

## 실행 명령 (`backend/`에서, JDK 21 필요)

- 실행: `./gradlew bootRun` (기본 프로필 `local`, H2 메모리 DB)
- dev 프로필: 루트에서 `docker compose up -d` 후 `./gradlew bootRun --args='--spring.profiles.active=dev'`
- 테스트: `./gradlew test` (자세한 명령은 `backend/CLAUDE.md`)
- Swagger UI: `http://localhost:8080/swagger-ui.html` (`local`에서만, D-07)
- Windows PowerShell 5.1에서는 `&&` 대신 `;`, `./gradlew` 대신 `.\gradlew.bat`

## 공통 규칙

- 비밀 값(비밀번호, 키, 접속 정보)은 코드와 설정 파일에 쓰지 않고 환경 변수로 받는다. `.env`는 커밋하지 않는다
- 새 환경 변수 키는 `.env.example`에 추가한다
- 새로 정한 사항은 `docs/requirements.md` 결정 사항 표에 다음 D-번호로 기록하고, 바뀐 결정은 상태 열(유효/대체/완료/재검토)을 고친다
- 작업은 `main`에서 브랜치를 만들어 하고, PR로 `main`에 병합한다
- 커밋 메시지: `type(scope): 설명` (예: `feat(course): …`, `docs: …`, `chore(dev): …`), 본문은 한국어
