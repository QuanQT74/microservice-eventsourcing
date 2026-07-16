$body = @{
    username = "demo123"
    email = "demo@example.com"
    password = "demo123"
    fullName = "Demo User"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "http://localhost:8085/api/v1/users" -Method Post -ContentType "application/json" -Body $body
    Write-Host "SUCCESS:"
    $response | ConvertTo-Json -Depth 3
} catch {
    Write-Host "ERROR: $($_.Exception.Response.StatusCode.value__)"
    $reader = [System.IO.StreamReader]::new($_.Exception.Response.GetResponseStream())
    $reader.ReadToEnd()
    $reader.Close()
}
