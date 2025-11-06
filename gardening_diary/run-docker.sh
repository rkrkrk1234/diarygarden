#!/bin/bash

# Firebase 자격 증명을 환경 변수로 설정
export FIREBASE_CREDENTIALS=$(cat src/main/resources/diarygarden-7bb2d-firebase-adminsdk-fbsvc-eb51c377a7.json)
export GCP_PROJECT_ID=diarygarden-7bb2d
export FIREBASE_SECRET_NAME=""

echo "환경 변수 설정 완료!"
echo "Docker 실행 중..."

docker-compose up --build






