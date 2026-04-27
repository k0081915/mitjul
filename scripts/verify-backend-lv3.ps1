param(
    [string]$BaseUrl = "http://localhost:8080"
)

$ErrorActionPreference = "Stop"

function Write-Step {
    param([string]$Message)
    Write-Host "[verify] $Message"
}

function Invoke-Json {
    param(
        [string]$Method = "Get",
        [string]$Path,
        [object]$Body = $null
    )

    $uri = "$BaseUrl$Path"
    if ($null -eq $Body) {
        return Invoke-RestMethod -Method $Method -Uri $uri
    }

    $json = $Body | ConvertTo-Json -Depth 10
    return Invoke-RestMethod -Method $Method -Uri $uri -ContentType "application/json" -Body $json
}

function Assert-True {
    param(
        [bool]$Condition,
        [string]$Message
    )

    if (-not $Condition) {
        throw "Assertion failed: $Message"
    }
}

Write-Step "Creating order for JSON export"
$order = Invoke-Json -Method Post -Path "/api/orders" -Body @{
    periodStart = "2026-04-01"
    periodEnd = "2026-04-30"
    coverStyle = "MINIMAL"
    ownerName = "Lv3 검증 사용자"
}
Assert-True ($order.id -ne $null) "Created order id should not be null"
Assert-True ($order.snapshotJson -ne $null) "Created order should include snapshotJson"

Write-Step "Downloading JSON export"
$exportResponse = Invoke-WebRequest -Method Get -Uri "$BaseUrl/api/orders/$($order.id)/export/json"
$contentType = [string]::Join("; ", $exportResponse.Headers["Content-Type"])
$contentDisposition = [string]::Join("; ", $exportResponse.Headers["Content-Disposition"])
Assert-True ($exportResponse.StatusCode -eq 200) "Export response should be 200"
Assert-True ($contentType -like "application/json*") "Export content type should be application/json"
Assert-True ($contentDisposition -like "*mitjul-order-$($order.orderNumber).json*") "Export filename should include order number"

$export = $exportResponse.Content | ConvertFrom-Json
Assert-True ($export.orderNumber -eq $order.orderNumber) "Export order number should match"
Assert-True ($export.status -eq $order.status) "Export status should match"
Assert-True ($export.exportedAt -ne $null) "ExportedAt should not be null"
Assert-True ($export.snapshot.ownerName -eq "Lv3 검증 사용자") "Snapshot ownerName should match"
Assert-True ($export.snapshot.bookCount -ge 1) "Snapshot should include at least one book"
Assert-True ($export.snapshot.quoteCount -ge 1) "Snapshot should include at least one quote"

Write-Step "Checking missing order export"
$missingFailed = $false
try {
    Invoke-WebRequest -Method Get -Uri "$BaseUrl/api/orders/999999/export/json" | Out-Null
} catch {
    $missingFailed = $_.Exception.Response.StatusCode.value__ -eq 404
}
Assert-True $missingFailed "Missing order export should return 404"

Write-Host "[verify] Backend Lv3 verification passed."
