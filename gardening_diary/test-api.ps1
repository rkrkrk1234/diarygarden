# API 테스트 스크립트

$baseUrl = "http://localhost:8080"

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "Gardening Diary API 테스트" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan

# 1. 헬스체크
Write-Host "`n[1] 헬스체크 테스트..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/api/health" -Method GET
    Write-Host "상태 코드: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "응답: $($response.Content)" -ForegroundColor Green
} catch {
    Write-Host "오류: $_" -ForegroundColor Red
}

# 2. API 정보
Write-Host "`n[2] API 정보 조회..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/api/info" -Method GET
    Write-Host "상태 코드: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "응답: $($response.Content)" -ForegroundColor Green
} catch {
    Write-Host "오류: $_" -ForegroundColor Red
}

# 3. 회원가입
Write-Host "`n[3] 회원가입 테스트..." -ForegroundColor Yellow
$registerData = @{
    username = "testuser_$(Get-Random)"
    password = "test1234"
    displayName = "테스트 사용자"
}
$registerBody = $registerData | ConvertTo-Json

try {
    $response = Invoke-WebRequest -Uri "$baseUrl/api/auth/register" `
        -Method POST `
        -ContentType "application/json" `
        -Body $registerBody
    
    Write-Host "상태 코드: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "응답: $($response.Content)" -ForegroundColor Green
    
    # 응답에서 토큰 추출 (JSON 파싱)
    $responseData = $response.Content | ConvertFrom-Json
    if ($responseData.data -and $responseData.data.token) {
        $token = $responseData.data.token
        Write-Host "토큰 받음: $($token.Substring(0, [Math]::Min(50, $token.Length)))..." -ForegroundColor Green
        
        # 4. 사용자 정보 조회
        Write-Host "`n[4] 사용자 정보 조회..." -ForegroundColor Yellow
        try {
            $headers = @{
                "Authorization" = "Bearer $token"
            }
            $response = Invoke-WebRequest -Uri "$baseUrl/api/auth/user" `
                -Method GET `
                -Headers $headers
            
            Write-Host "상태 코드: $($response.StatusCode)" -ForegroundColor Green
            Write-Host "응답: $($response.Content)" -ForegroundColor Green
        } catch {
            Write-Host "오류: $_" -ForegroundColor Red
        }
        
        # 5. 다이어리 생성
        Write-Host "`n[5] 다이어리 생성 테스트..." -ForegroundColor Yellow
        $diaryData = @{
            treeId = "tree_123"
            content = "오늘 식물에게 물을 주었습니다. 잘 자라길 바랍니다!"
        }
        $diaryBody = $diaryData | ConvertTo-Json
        
        try {
            $headers = @{
                "Authorization" = "Bearer $token"
            }
            $response = Invoke-WebRequest -Uri "$baseUrl/api/diaries" `
                -Method POST `
                -ContentType "application/json" `
                -Body $diaryBody `
                -Headers $headers
            
            Write-Host "상태 코드: $($response.StatusCode)" -ForegroundColor Green
            Write-Host "응답: $($response.Content)" -ForegroundColor Green
            
            # 다이어리 ID 추출
            $diaryData = $response.Content | ConvertFrom-Json
            if ($diaryData.data -and $diaryData.data.id) {
                $diaryId = $diaryData.data.id
                Write-Host "생성된 다이어리 ID: $diaryId" -ForegroundColor Green
                
                # 6. 다이어리 조회
                Write-Host "`n[6] 다이어리 조회 (ID로)..." -ForegroundColor Yellow
                try {
                    $response = Invoke-WebRequest -Uri "$baseUrl/api/diaries/$diaryId" -Method GET
                    Write-Host "상태 코드: $($response.StatusCode)" -ForegroundColor Green
                    Write-Host "응답: $($response.Content)" -ForegroundColor Green
                } catch {
                    Write-Host "오류: $_" -ForegroundColor Red
                }
                
                # 7. 다이어리 목록 조회
                Write-Host "`n[7] 사용자의 다이어리 목록 조회..." -ForegroundColor Yellow
                try {
                    $headers = @{
                        "Authorization" = "Bearer $token"
                    }
                    $response = Invoke-WebRequest -Uri "$baseUrl/api/diaries" `
                        -Method GET `
                        -Headers $headers
                    
                    Write-Host "상태 코드: $($response.StatusCode)" -ForegroundColor Green
                    Write-Host "응답: $($response.Content)" -ForegroundColor Green
                } catch {
                    Write-Host "오류: $_" -ForegroundColor Red
                }
                
                # 8. 다이어리 개수 조회
                Write-Host "`n[8] 다이어리 개수 조회..." -ForegroundColor Yellow
                try {
                    $headers = @{
                        "Authorization" = "Bearer $token"
                    }
                    $response = Invoke-WebRequest -Uri "$baseUrl/api/diaries/count" `
                        -Method GET `
                        -Headers $headers
                    
                    Write-Host "상태 코드: $($response.StatusCode)" -ForegroundColor Green
                    Write-Host "응답: $($response.Content)" -ForegroundColor Green
                } catch {
                    Write-Host "오류: $_" -ForegroundColor Red
                }
            }
        } catch {
            Write-Host "오류: $_" -ForegroundColor Red
            Write-Host "응답: $($_.ErrorDetails.Message)" -ForegroundColor Red
        }
    }
} catch {
    Write-Host "오류: $_" -ForegroundColor Red
    Write-Host "응답: $($_.ErrorDetails.Message)" -ForegroundColor Red
}

Write-Host "`n==========================================" -ForegroundColor Cyan
Write-Host "테스트 완료" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan

