# Gemini Code Review Style Guide

## General
- 답변은 한국어로 작성한다.
- 단순 취향보다 버그 가능성, 보안, 성능, 유지보수성을 우선 리뷰한다.
- 변경된 코드 기준으로 실제 문제가 있는 경우에만 코멘트한다.

## Frontend
- TypeScript 타입 안정성을 우선한다.
- React 컴포넌트는 불필요한 리렌더링, 중복 상태, 부적절한 side effect를 확인한다.
- API 호출 로직은 에러 처리와 로딩 상태 처리를 확인한다.

## Backend
- Java/Spring 코드에서는 트랜잭션, 예외 처리, DTO/entity 분리, 입력 검증을 확인한다.
- DB 접근 로직에서 N+1, 불필요한 쿼리, 보안 이슈를 확인한다.

## Docker / Infra
- docker-compose 설정에서 민감정보 하드코딩 여부를 확인한다.
- 로컬 개발과 배포 환경 설정이 섞이지 않았는지 확인한다.