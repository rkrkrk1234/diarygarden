# 🚀 GCP Cloud Run 배포 가이드 (프로젝트 맞춤)

이 가이드는 **gardening-diary** 프로젝트를 GCP Cloud Run에 배포하는 방법을 설명합니다.

## 📋 사전 준비

### 1. GCP 프로젝트 정보
- **프로젝트 ID**: `diarygarden-7bb2d`
- **리전**: `asia-northeast3` (서울)
- **Firebase 키 파일**: `src/main/resources/diarygarden-7bb2d-firebase-adminsdk-fbsvc-eb51c377a7.json`

### 2. 필요한 API 활성화
```bash
gcloud services enable \
  cloudbuild.googleapis.com \
  run.googleapis.com \
  secretmanager.googleapis.com \
  artifactregistry.googleapis.com \
  containerregistry.googleapis.com
```

---

## 방법 1: Artifact Registry 사용 (권장)

Artifact Registry는 Container Registry의 최신 대체제입니다.

### 1단계: Artifact Registry 저장소 생성

```bash
gcloud artifacts repositories create gardening-diary-repo \
    --repository-format=docker \
    --location=asia-northeast3 \
    --description="Gardening Diary Docker repository"
```

### 2단계: Docker 인증 설정

```bash
gcloud auth configure-docker asia-northeast3-docker.pkg.dev
```

### 3단계: Docker 이미지 빌드 및 태그 지정

```bash
cd DiaryGarden-BE/gardening_diary

# Dockerfile.prod 사용하여 빌드
docker build -t asia-northeast3-docker.pkg.dev/diarygarden-7bb2d/gardening-diary-repo/gardening-diary:1.0 \
    -f Dockerfile.prod .
```

### 4단계: 이미지 푸시

```bash
docker push asia-northeast3-docker.pkg.dev/diarygarden-7bb2d/gardening-diary-repo/gardening-diary:1.0
```

### 5단계: Secret Manager에 Firebase 키 저장

```bash
# Secret 생성 (처음만)
gcloud secrets create firebase-credentials \
    --data-file=src/main/resources/diarygarden-7bb2d-firebase-adminsdk-fbsvc-eb51c377a7.json

# 또는 기존 Secret에 새 버전 추가
gcloud secrets versions add firebase-credentials \
    --data-file=src/main/resources/diarygarden-7bb2d-firebase-adminsdk-fbsvc-eb51c377a7.json
```

### 6단계: Cloud Run에 배포

```bash
gcloud run deploy gardening-diary \
    --image asia-northeast3-docker.pkg.dev/diarygarden-7bb2d/gardening-diary-repo/gardening-diary:1.0 \
    --region asia-northeast3 \
    --platform managed \
    --allow-unauthenticated \
    --set-env-vars SPRING_PROFILES_ACTIVE=prod,GCP_PROJECT_ID=diarygarden-7bb2d \
    --set-secrets FIREBASE_CREDENTIALS=firebase-credentials:latest \
    --memory 512Mi \
    --cpu 1 \
    --max-instances 10 \
    --min-instances 0 \
    --port 8080
```

### 7단계: 서비스 계정 권한 설정 (중요!)

Cloud Run 서비스 계정에 Secret Manager 접근 권한이 필요합니다:

```bash
# 프로젝트 번호 확인
PROJECT_NUMBER=$(gcloud projects describe diarygarden-7bb2d --format='value(projectNumber)')

# Secret Manager 접근 권한 부여
gcloud secrets add-iam-policy-binding firebase-credentials \
    --member="serviceAccount:${PROJECT_NUMBER}-compute@developer.gserviceaccount.com" \
    --role="roles/secretmanager.secretAccessor"
```

---

## 방법 2: 기존 스크립트 사용 (Container Registry)

프로젝트에 이미 `deploy-gcp.sh` 스크립트가 있습니다.

### 실행 방법

```bash
cd DiaryGarden-BE/gardening_diary

# 환경 변수 설정
export GCP_PROJECT_ID=diarygarden-7bb2d
export FIREBASE_KEY_FILE=src/main/resources/diarygarden-7bb2d-firebase-adminsdk-fbsvc-eb51c377a7.json

# 배포 스크립트 실행
chmod +x deploy-gcp.sh
./deploy-gcp.sh
```

이 스크립트는 다음을 자동으로 수행합니다:
1. GCP 프로젝트 설정
2. Secret Manager에 Firebase 키 저장
3. Docker 이미지 빌드 (Dockerfile.prod 사용)
4. Container Registry에 이미지 푸시
5. Cloud Run에 배포

---

## 방법 3: Cloud Build 사용 (CI/CD)

프로젝트에 `cloudbuild.yaml` 파일이 있습니다.

### 실행 방법

```bash
cd DiaryGarden-BE/gardening_diary

# Cloud Build로 빌드 및 배포
gcloud builds submit --config cloudbuild.yaml .
```

이 방법은 Cloud Build가 자동으로:
1. Docker 이미지 빌드
2. Container Registry에 푸시
3. Cloud Run에 배포

를 수행합니다.

---

## 🔒 GCP 콘솔에서 환경 변수 설정 (수동 배포 시)

Cloud Run 콘솔에서 배포하는 경우:

### 1. 환경 변수 설정
- **변수 추가** 클릭
- 이름: `SPRING_PROFILES_ACTIVE`, 값: `prod`
- 이름: `GCP_PROJECT_ID`, 값: `diarygarden-7bb2d`

### 2. 보안 비밀 연결
- **보안 비밀** 탭에서 **참조 추가**
- 보안 비밀: `firebase-credentials`
- 버전: `latest`
- 참조 방법: **환경 변수로 노출**
- 변수 이름: `FIREBASE_CREDENTIALS`

### 3. 포트 설정
- 컨테이너 포트: `8080`
- 인증: **인증되지 않은 호출 허용**

---

## ✅ 배포 확인

### 서비스 URL 확인

```bash
gcloud run services describe gardening-diary \
    --region asia-northeast3 \
    --format='value(status.url)'
```

### 헬스체크 테스트

```bash
# 배포된 URL
SERVICE_URL=$(gcloud run services describe gardening-diary \
    --region asia-northeast3 \
    --format='value(status.url)')

curl $SERVICE_URL/api/health
```

### 로그 확인

```bash
# 실시간 로그 보기
gcloud run services logs read gardening-diary --region asia-northeast3 --follow
```

---

## 🐛 문제 해결

### 1. SIGSEGV 오류 (Exit code 139)
- ✅ 해결됨: Dockerfile.prod에서 Alpine 대신 Debian 기반 이미지 사용
- ✅ 해결됨: curl 설치 및 헬스체크 설정

### 2. Secret Manager 접근 권한 오류
```bash
# 서비스 계정에 권한 부여 (위의 7단계 참고)
PROJECT_NUMBER=$(gcloud projects describe diarygarden-7bb2d --format='value(projectNumber)')
gcloud secrets add-iam-policy-binding firebase-credentials \
    --member="serviceAccount:${PROJECT_NUMBER}-compute@developer.gserviceaccount.com" \
    --role="roles/secretmanager.secretAccessor"
```

### 3. 이미지 푸시 실패
```bash
# Docker 인증 확인
gcloud auth configure-docker asia-northeast3-docker.pkg.dev
# 또는
gcloud auth configure-docker gcr.io
```

### 4. 로그 확인
```bash
# Cloud Run 로그
gcloud run services logs read gardening-diary --region asia-northeast3

# 또는 GCP 콘솔에서
# Cloud Run > gardening-diary > 로그 탭
```

---

## 📝 주요 파일 설명

- **Dockerfile.prod**: GCP 배포용 Dockerfile (Firebase 키 파일 제외)
- **deploy-gcp.sh**: 자동 배포 스크립트 (Container Registry 사용)
- **cloudbuild.yaml**: Cloud Build 설정 파일 (CI/CD용)

---

## 🎯 빠른 배포 명령어 요약

### Artifact Registry 사용 (권장)
```bash
# 1. 저장소 생성 (처음만)
gcloud artifacts repositories create gardening-diary-repo \
    --repository-format=docker --location=asia-northeast3

# 2. 인증
gcloud auth configure-docker asia-northeast3-docker.pkg.dev

# 3. 빌드 및 푸시
docker build -t asia-northeast3-docker.pkg.dev/diarygarden-7bb2d/gardening-diary-repo/gardening-diary:1.0 -f Dockerfile.prod .
docker push asia-northeast3-docker.pkg.dev/diarygarden-7bb2d/gardening-diary-repo/gardening-diary:1.0

# 4. Secret 생성 (처음만)
gcloud secrets create firebase-credentials \
    --data-file=src/main/resources/diarygarden-7bb2d-firebase-adminsdk-fbsvc-eb51c377a7.json

# 5. 배포
gcloud run deploy gardening-diary \
    --image asia-northeast3-docker.pkg.dev/diarygarden-7bb2d/gardening-diary-repo/gardening-diary:1.0 \
    --region asia-northeast3 --platform managed --allow-unauthenticated \
    --set-env-vars SPRING_PROFILES_ACTIVE=prod,GCP_PROJECT_ID=diarygarden-7bb2d \
    --set-secrets FIREBASE_CREDENTIALS=firebase-credentials:latest \
    --memory 512Mi --cpu 1 --port 8080
```

### 기존 스크립트 사용
```bash
export GCP_PROJECT_ID=diarygarden-7bb2d
export FIREBASE_KEY_FILE=src/main/resources/diarygarden-7bb2d-firebase-adminsdk-fbsvc-eb51c377a7.json
./deploy-gcp.sh
```




