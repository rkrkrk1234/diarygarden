# Simple API Test Script

$baseUrl = "http://localhost:8080"

Write-Host "=== API Health Check ===" -ForegroundColor Cyan
$response1 = Invoke-WebRequest -Uri "$baseUrl/api/health" -UseBasicParsing
Write-Host "Status: $($response1.StatusCode)"
Write-Host "Content: $($response1.Content)"
Write-Host ""

Write-Host "=== API Info ===" -ForegroundColor Cyan
$response2 = Invoke-WebRequest -Uri "$baseUrl/api/info" -UseBasicParsing
Write-Host "Status: $($response2.StatusCode)"
Write-Host "Content: $($response2.Content)"
Write-Host ""

Write-Host "=== Register Test ===" -ForegroundColor Cyan
$registerJson = '{"username":"testuser' + (Get-Random) + '","password":"test1234","displayName":"Test User"}'
try {
    $response3 = Invoke-WebRequest -Uri "$baseUrl/api/auth/register" `
        -Method POST `
        -ContentType "application/json" `
        -Body $registerJson `
        -UseBasicParsing
    Write-Host "Status: $($response3.StatusCode)"
    Write-Host "Content: $($response3.Content)"
    
    $responseObj = $response3.Content | ConvertFrom-Json
    if ($responseObj.data -and $responseObj.data.token) {
        $token = $responseObj.data.token
        Write-Host "Token received: $($token.Substring(0, [Math]::Min(50, $token.Length)))..."
        
        Write-Host ""
        Write-Host "=== Get User Info ===" -ForegroundColor Cyan
        $headers = @{"Authorization" = "Bearer $token"}
        $response4 = Invoke-WebRequest -Uri "$baseUrl/api/auth/user" `
            -Method GET `
            -Headers $headers `
            -UseBasicParsing
        Write-Host "Status: $($response4.StatusCode)"
        Write-Host "Content: $($response4.Content)"
        
        Write-Host ""
        Write-Host "=== Create Diary ===" -ForegroundColor Cyan
        $diaryJson = '{"treeId":"tree_123","content":"Today I watered my plant!"}'
        $response5 = Invoke-WebRequest -Uri "$baseUrl/api/diaries" `
            -Method POST `
            -ContentType "application/json" `
            -Body $diaryJson `
            -Headers $headers `
            -UseBasicParsing
        Write-Host "Status: $($response5.StatusCode)"
        Write-Host "Content: $($response5.Content)"
        
        $diaryObj = $response5.Content | ConvertFrom-Json
        if ($diaryObj.data -and $diaryObj.data.id) {
            $diaryId = $diaryObj.data.id
            
            Write-Host ""
            Write-Host "=== Get Diary by ID ===" -ForegroundColor Cyan
            $response6 = Invoke-WebRequest -Uri "$baseUrl/api/diaries/$diaryId" -UseBasicParsing
            Write-Host "Status: $($response6.StatusCode)"
            Write-Host "Content: $($response6.Content)"
            
            Write-Host ""
            Write-Host "=== Get User Diaries ===" -ForegroundColor Cyan
            $response7 = Invoke-WebRequest -Uri "$baseUrl/api/diaries" `
                -Method GET `
                -Headers $headers `
                -UseBasicParsing
            Write-Host "Status: $($response7.StatusCode)"
            Write-Host "Content: $($response7.Content)"
            
            Write-Host ""
            Write-Host "=== Get Diary Count ===" -ForegroundColor Cyan
            $response8 = Invoke-WebRequest -Uri "$baseUrl/api/diaries/count" `
                -Method GET `
                -Headers $headers `
                -UseBasicParsing
            Write-Host "Status: $($response8.StatusCode)"
            Write-Host "Content: $($response8.Content)"
        }
    }
} catch {
    Write-Host "Error: $_" -ForegroundColor Red
    if ($_.ErrorDetails.Message) {
        Write-Host "Details: $($_.ErrorDetails.Message)" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "=== Test Complete ===" -ForegroundColor Green







