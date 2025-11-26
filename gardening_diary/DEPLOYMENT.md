# GCP Cloud Run 배포 가이드

## 사전 준비

1. **GCP 프로젝트 생성 및 설정**
   ```bash
   gcloud config set project diarygarden-7bb2d
   ```

2. **필요한 API 활성화**
   ```bash
   gcloud services enable \
     cloudbuild.googleapis.com \
     run.googleapis.com \
     secretmanager.googleapis.com \
     containerregistry.googleapis.com
   ```

3. **Firebase 자격 증명 파일 준비**
   - 파일 위치: `src/main/resources/diarygarden-7bb2d-firebase-adminsdk-fbsvc-eb51c377a7.json`

## 배포 방법

### 방법 1: 배포 스크립트 사용 (권장)

```bash
cd DiaryGarden-BE/gardening_diary

# 환경 변수 설정
export GCP_PROJECT_ID=diarygarden-7bb2d
export FIREBASE_KEY_FILE=src/main/resources/diarygarden-7bb2d-firebase-adminsdk-fbsvc-eb51c377a7.json

# 배포 스크립트 실행
chmod +x deploy-gcp.sh
./deploy-gcp.sh
```

### 방법 2: Cloud Build 사용 (CI/CD)

```bash
# Cloud Build로 빌드 및 배포
gcloud builds submit --config cloudbuild.yaml .
```

### 방법 3: 수동 배포

```bash
# 1. GCP 프로젝트 설정
gcloud config set project diarygarden-7bb2d

# 2. Secret Manager에 Firebase 자격 증명 저장
gcloud secrets create firebase-credentials \
  --data-file=src/main/resources/diarygarden-7bb2d-firebase-adminsdk-fbsvc-eb51c377a7.json

# 3. Docker 이미지 빌드 및 푸시
docker build -t gcr.io/diarygarden-7bb2d/gardening-diary:latest -f Dockerfile.prod .
docker push gcr.io/diarygarden-7bb2d/gardening-diary:latest

# 4. Cloud Run에 배포
gcloud run deploy gardening-diary \
  --image gcr.io/diarygarden-7bb2d/gardening-diary:latest \
  --region asia-northeast3 \
  --platform managed \
  --allow-unauthenticated \
  --set-env-vars SPRING_PROFILES_ACTIVE=prod,GCP_PROJECT_ID=diarygarden-7bb2d \
  --set-secrets FIREBASE_CREDENTIALS=firebase-credentials:latest \
  --memory 512Mi \
  --cpu 1 \
  --max-instances 10
```

## 배포 후 확인

```bash
# 서비스 URL 확인
gcloud run services describe gardening-diary \
  --region asia-northeast3 \
  --format='value(status.url)'

# 로그 확인
gcloud run services logs read gardening-diary --region asia-northeast3
```

## 문제 해결

### Secret Manager 권한 설정
Cloud Run 서비스 계정에 Secret Manager 접근 권한이 필요합니다:

```bash
PROJECT_NUMBER=$(gcloud projects describe diarygarden-7bb2d --format='value(projectNumber)')
gcloud secrets add-iam-policy-binding firebase-credentials \
  --member="serviceAccount:${PROJECT_NUMBER}-compute@developer.gserviceaccount.com" \
  --role="roles/secretmanager.secretAccessor"
```

### Container Registry 인증
```bash
gcloud auth configure-docker
```









