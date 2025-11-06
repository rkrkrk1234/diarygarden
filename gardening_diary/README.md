# Gardening Diary Backend

Spring Boot 기반의 정원 일기 백엔드 애플리케이션입니다.

## 설정 방법

### 1. Firebase 설정

1. Firebase Console에서 프로젝트 생성
2. 서비스 계정 키 다운로드 (JSON 파일)
3. 환경변수 설정:

```bash
# Windows
set FIREBASE_CREDENTIALS={"type":"service_account","project_id":"your-project-id",...}

# Linux/Mac
export FIREBASE_CREDENTIALS='{"type":"service_account","project_id":"your-project-id",...}'
```

### 2. 애플리케이션 실행

```bash
./gradlew bootRun
```

### 3. API 테스트

```bash
# 회원가입
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123",
    "displayName": "Test User"
  }'
```

## 환경변수

- `FIREBASE_CREDENTIALS`: Firebase 서비스 계정 JSON
- `GCP_PROJECT_ID`: GCP 프로젝트 ID
- `JWT_SECRET`: JWT 시크릿 키





