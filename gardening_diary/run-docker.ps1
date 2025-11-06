# Docker 실행 스크립트
$jsonPath = "src\main\resources\diarygarden-7bb2d-firebase-adminsdk-fbsvc-eb51c377a7.json"

if (Test-Path $jsonPath) {
    # JSON 파일을 읽어서 한 줄로 변환 (줄바꿈 제거)
    $jsonContent = Get-Content $jsonPath -Raw
    $jsonContent = $jsonContent -replace "`r`n", "" -replace "`n", "" -replace "`r", "" -replace " ", ""
    $env:FIREBASE_CREDENTIALS = $jsonContent
    Write-Host "Firebase 자격 증명 로드 완료!" -ForegroundColor Green
} else {
    Write-Host "경고: Firebase 자격 증명 파일을 찾을 수 없습니다: $jsonPath" -ForegroundColor Yellow
}

$env:GCP_PROJECT_ID = "diarygarden-7bb2d"
$env:FIREBASE_SECRET_NAME = ""

Write-Host "환경 변수 설정 완료!" -ForegroundColor Green
Write-Host "Docker 실행 중..." -ForegroundColor Cyan

docker-compose up --build



