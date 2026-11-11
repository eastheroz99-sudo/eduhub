# frontend

- UI 컴포넌트는 MUI를 사용한다.
- API 호출은 OpenAPI 스펙에서 생성한 Axios 클라이언트를 쓴다 (NFR-04).
- 서버 데이터 조회는 커스텀 훅(`src/hooks/useXxx.ts`)으로 감싸고, 화면 컴포넌트는 API를 직접 부르지 않는다.
