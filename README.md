# Gardening Diary Backend

> **프로젝트 개요**: 정원 일기 관리 백엔드 API 서버  
> 사용자가 정원과 나무를 관리하고 일기를 작성하며, AI 기반 감정 분석을 통해 일기 내용을 분석하는 RESTful API 서버입니다.

---

## 목차

- [프로젝트 소개](#프로젝트-소개)
- [기술 스택 및 선택 이유](#기술-스택-및-선택-이유)
- [시스템 아키텍처](#시스템-아키텍처)
- [주요 기능](#주요-기능)
- [핵심 구현 내용](#핵심-구현-내용)
- [프로젝트 구조](#프로젝트-구조)
- [시작하기](#시작하기)
- [API 문서](#api-문서)
- [배포](#배포)
- [개발 경험 및 개선 사항](#개발-경험-및-개선-사항)

---

## 프로젝트 소개

### 프로젝트 목적
사용자가 정원을 가상으로 꾸미고, 나무를 심으며, 일기를 작성할 수 있는 웹 애플리케이션의 백엔드 서버입니다. 일기 작성 시 AI 기반 감정 분석을 통해 사용자의 감정 상태를 파악하고 시각화합니다.

### 주요 특징
- **Firebase Authentication** 기반 사용자 인증 (이메일/비밀번호, Google OAuth)
- **Firestore** NoSQL 데이터베이스 활용
- **AI 감정 분석 API** 연동 (비동기 처리)
- **RESTful API** 설계 및 Swagger 문서화
- **Google Cloud Run** 컨테이너 기반 배포

---

## 기술 스택

### Backend Framework
- **Spring Boot 3.5.6** + **Java 17**

### Database
- **Google Cloud Firestore**

### Authentication
- **Firebase Authentication**

### Security
- **Spring Security** + **JWT** + **Firebase ID Token**

### API Documentation
- **SpringDoc OpenAPI (Swagger UI)**

### Deployment
- **Google Cloud Run** + **Docker**

---

## 시스템 아키텍처

### 아키텍처 다이어그램

```
┌─────────────┐
│   Client    │ (Web/Mobile)
└──────┬──────┘
       │ HTTPS
       ▼
┌─────────────────────────────────────┐
│      Spring Boot Application         │
│  ┌────────────────────────────────┐  │
│  │  Security Filter Chain          │  │
│  │  - FirebaseTokenFilter         │  │
│  │  - JWT Validation              │  │
│  └────────────────────────────────┘  │
│  ┌────────────────────────────────┐  │
│  │  Controllers (REST API)        │  │
│  │  - AuthController              │  │
│  │  - DiaryController             │  │
│  │  - GardenController            │  │
│  └────────────────────────────────┘  │
│  ┌────────────────────────────────┐  │
│  │  Service Layer                │  │
│  │  - Business Logic              │  │
│  │  - External API Integration    │  │
│  └────────────────────────────────┘  │
│  ┌────────────────────────────────┐  │
│  │  Repository Layer              │  │
│  │  - Firestore Operations        │  │
│  └────────────────────────────────┘  │
└──────────┬───────────────────────────┘
           │
    ┌──────┴──────┐
    │             │
    ▼             ▼
┌─────────┐  ┌──────────────┐
│Firestore│  │ Emotion AI   │
│Database │  │ API Service │
└─────────┘  └──────────────┘
```

### 레이어드 아키텍처

1. **Controller Layer**: HTTP 요청/응답 처리, 입력 검증
2. **Service Layer**: 비즈니스 로직, 트랜잭션 관리
3. **Repository Layer**: Firestore 데이터 접근 추상화
4. **Entity Layer**: 도메인 모델 정의

---

## 주요 기능

### 1. 인증 및 사용자 관리
- 회원가입/로그인 (이메일/비밀번호)
- Google OAuth 로그인
- Firebase ID Token 검증
- JWT 토큰 발급 (자체 인증용)
- 사용자 정보 CRUD
- 회원 탈퇴

### 2. 정원 관리
- 정원 생성, 조회, 수정, 삭제
- 사용자별 정원 목록 조회

### 3. 나무 관리
- 나무 생성, 조회, 수정, 삭제
- 정원별 나무 목록 조회

### 4. 일기 관리
- 일기 작성, 조회, 수정, 삭제
- 사용자별 일기 목록 조회 (페이징 지원)
- 나무별 일기 조회

### 5. 감정 분석
- AI 기반 일기 내용 감정 분석 (비동기)
- 감정 분석 결과 조회
- 외부 API 장애 시 Fallback 처리

---

## 핵심 구현 내용

### 1. 이중 토큰 인증 시스템

**문제**: 클라이언트는 Firebase SDK를 사용하고, 서버는 자체 JWT도 발급해야 함

**해결**: `FirebaseTokenFilter`에서 두 가지 토큰 모두 검증

```java
// JWT 토큰 검증 시도
try {
    uid = jwtProvider.getUidFromToken(token);
} catch (Exception jwtEx) {
    // 실패 시 Firebase ID Token으로 시도
    FirebaseToken decoded = FirebaseAuth.getInstance().verifyIdToken(token);
    uid = decoded.getUid();
}
```

**장점**:
- 클라이언트 유연성 확보 (Firebase SDK 또는 자체 인증 모두 지원)
- 기존 Firebase 사용자와 신규 사용자 모두 호환

### 2. Firestore Repository 패턴

**문제**: Firestore는 JPA와 달리 Spring Data 지원이 제한적

**해결**: 커스텀 Repository 클래스로 데이터 접근 계층 추상화

```java
@Repository
public class DiaryRepository {
    @Autowired
    private Firestore firestore;
    
    public Diary save(Diary diary) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> future = firestore.collection(COLLECTION_NAME)
                .document(diary.getId())
                .set(diary);
        future.get(); // 비동기 작업 동기화
        return diary;
    }
}
```

**장점**:
- Service Layer와 데이터베이스 구현 분리
- 향후 다른 NoSQL로 전환 시 유연성 확보


### 3. 환경 변수 관리

**문제**: Firebase 자격 증명 등 민감한 정보를 코드에 포함하면 안 됨

**해결**: 
- `.env` 파일 + `java-dotenv` 라이브러리
- GCP Secret Manager 연동 (프로덕션)
- 환경별 설정 분리

```java
// .env 파일 자동 로드
Dotenv dotenv = Dotenv.configure()
        .directory("./")
        .ignoreIfMissing()
        .load();
```

### 4. CORS 및 보안 설정

**구현**:
- 개발 환경: 모든 오리진 허용 (유연한 개발)
- 프로덕션: 특정 도메인만 허용 (보안 강화)
- Stateless 세션 관리 (JWT 기반)

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOriginPatterns(Arrays.asList("*"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowCredentials(true);
    return source;
}
```

---

## 프로젝트 구조

```
gardening_diary/
├── src/
│   ├── main/
│   │   ├── java/com/GDG/worktree/team2/gardening_diary/
│   │   │   ├── config/              # 설정 클래스
│   │   │   │   ├── FirebaseConfig.java          # Firebase 초기화
│   │   │   │   ├── FirestoreConfig.java         # Firestore 연결
│   │   │   │   ├── SecurityConfig.java         # Spring Security 설정
│   │   │   │   ├── FirebaseTokenFilter.java    # 토큰 검증 필터
│   │   │   │   └── OpenApiConfig.java          # Swagger 설정
│   │   │   ├── controller/          # REST API 컨트롤러
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── DiaryController.java
│   │   │   │   ├── GardenController.java
│   │   │   │   ├── TreeController.java
│   │   │   │   ├── EmotionAnalysisController.java
│   │   │   │   └── HealthController.java
│   │   │   ├── service/             # 비즈니스 로직
│   │   │   │   ├── AuthService.java
│   │   │   │   ├── DiaryService.java
│   │   │   │   ├── EmotionAnalysisService.java
│   │   │   │   └── ...
│   │   │   ├── repository/          # 데이터 접근 계층
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── DiaryRepository.java
│   │   │   │   └── ...
│   │   │   ├── entity/              # 도메인 모델
│   │   │   │   ├── User.java
│   │   │   │   ├── Diary.java
│   │   │   │   ├── Garden.java
│   │   │   │   └── ...
│   │   │   ├── dto/                 # 데이터 전송 객체
│   │   │   │   ├── ApiResponse.java
│   │   │   │   ├── AuthResponse.java
│   │   │   │   └── ...
│   │   │   └── security/            # 보안 관련
│   │   │       └── JwtProvider.java
│   │   └── resources/
│   │       ├── application.yml      # 애플리케이션 설정
│   │       └── static/              # 정적 리소스
│   └── test/                        # 테스트 코드
├── build.gradle                     # Gradle 빌드 설정
├── docker-compose.yml              # Docker Compose 설정
├── Dockerfile                      # Docker 이미지 빌드 파일
├── Dockerfile.prod                 # 프로덕션용 Dockerfile
└── README.md                       # 프로젝트 문서
```

---

## 시작하기

### 사전 요구사항

- Java 17 이상
- Gradle 7.x 이상
- Firebase 프로젝트
- Google Cloud 프로젝트 (Firestore 사용)

### 1. 저장소 클론

```bash
git clone <repository-url>
cd gardening_diary
```

### 2. Firebase 설정

1. [Firebase Console](https://console.firebase.google.com/)에서 프로젝트 생성
2. Authentication 활성화 (이메일/비밀번호, Google)
3. Firestore Database 생성
4. 서비스 계정 키 다운로드

### 3. 환경 변수 설정

프로젝트 루트에 `.env` 파일을 생성:

```bash
# Firebase 설정
FIREBASE_CREDENTIALS={"type":"service_account","project_id":"your-project-id",...}

# GCP 프로젝트 ID
GCP_PROJECT_ID=your-project-id

# JWT 설정
JWT_SECRET=your-jwt-secret-key
JWT_EXPIRATION=86400000

# 감정 분석 API 설정 (선택사항)
EMOTION_API_URL=http://34.22.105.129:8080/api/v1/inference
EMOTION_API_KEY=your-emotion-api-key
```

### 4. 애플리케이션 실행

```bash
# Gradle Wrapper를 사용한 실행
./gradlew bootRun

# 또는 Windows에서
gradlew.bat bootRun
```

애플리케이션이 성공적으로 시작되면 `http://localhost:8080`에서 접근할 수 있습니다.

---

## API 문서

### Swagger UI

애플리케이션 실행 후 다음 URL에서 API 문서를 확인할 수 있습니다:

```
http://localhost:8080/swagger-ui.html
```

### 주요 API 엔드포인트

#### 인증 (`/api/auth`)
- `POST /api/auth/register` - 회원가입
- `POST /api/auth/login` - 로그인
- `POST /api/auth/google` - Google 로그인
- `POST /api/auth/verify` - 토큰 검증
- `GET /api/auth/user` - 사용자 정보 조회
- `PUT /api/auth/user` - 사용자 정보 수정
- `DELETE /api/auth/user` - 회원 탈퇴

#### 정원 관리 (`/api/gardens`)
- `POST /api/gardens` - 정원 생성
- `GET /api/gardens` - 정원 목록 조회
- `GET /api/gardens/{id}` - 정원 상세 조회
- `PUT /api/gardens/{id}` - 정원 수정
- `DELETE /api/gardens/{id}` - 정원 삭제

#### 나무 관리 (`/api/trees`)
- `POST /api/trees` - 나무 생성
- `GET /api/trees` - 나무 목록 조회
- `GET /api/trees/{id}` - 나무 상세 조회
- `PUT /api/trees/{id}` - 나무 수정
- `DELETE /api/trees/{id}` - 나무 삭제

#### 일기 관리 (`/api/diaries`)
- `POST /api/diaries` - 일기 작성
- `GET /api/diaries` - 일기 목록 조회 (페이징 지원)
- `GET /api/diaries/{id}` - 일기 상세 조회
- `PUT /api/diaries/{id}` - 일기 수정
- `DELETE /api/diaries/{id}` - 일기 삭제

#### 감정 분석 (`/api/emotions`)
- `GET /api/emotions/diary/{diaryId}` - 일기 감정 분석 결과 조회

#### 헬스체크 (`/api`)
- `GET /api/health` - 서비스 상태 확인

---

## 배포

### Docker를 사용한 배포

#### 로컬 Docker 실행

```bash
# Docker 이미지 빌드
docker build -t gardening-diary -f Dockerfile.prod .

# Docker 컨테이너 실행
docker run -p 8080:8080 \
  -e FIREBASE_CREDENTIALS='{"type":"service_account",...}' \
  -e GCP_PROJECT_ID=your-project-id \
  -e JWT_SECRET=your-secret \
  gardening-diary
```

### Google Cloud Run 배포

자세한 배포 가이드는 `GCP_DEPLOYMENT_GUIDE.md` 파일을 참고하세요.

```bash
# GCP 프로젝트 설정
gcloud config set project your-project-id

# Cloud Run에 배포
gcloud run deploy gardening-diary \
  --source . \
  --platform managed \
  --region asia-northeast3 \
  --allow-unauthenticated
```

**배포 특징**:
- Secret Manager를 통한 민감 정보 관리
- 자동 스케일링 (트래픽에 따라 인스턴스 자동 조정)
- HTTPS 자동 제공

---



---

## 기술 스택 요약

| 카테고리 | 기술 |
|---------|------|
| **Framework** | Spring Boot 3.5.6 |
| **Language** | Java 17 |
| **Build Tool** | Gradle |
| **Database** | Google Cloud Firestore |
| **Authentication** | Firebase Authentication |
| **Security** | Spring Security, JWT |
| **API Docs** | SpringDoc OpenAPI (Swagger UI) |
| **Deployment** | Google Cloud Run, Docker |
| **External API** | AI 감정 분석 API |



